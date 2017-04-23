package com.mail.dispatcher.services.mail;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import com.mail.dispatcher.persistence.MailRepository;
import com.mail.dispatcher.services.file.FileService;
import com.mail.dispatcher.util.MailUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 20.04.2017.
 */
@Service("mailService")
@Transactional
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final Integer CAPASITY = 100;
    private static final Integer LIMIT = CAPASITY / 10;
    @Value("${spring.mail.username}")
    private String FROM;

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(CAPASITY);
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FileService fileService;

    @PostConstruct
    private void queueProcessing() {
        executorService.submit(() -> {
            LOG.info("Start scheduling task");
            while (true) {
                String id = queue.take();
                LOG.info("Id {} was taken out of queue", id);
                Mail mail = get(id);
                send(mail);
            }
        });
    }

    @Override
    public Mail save(Mail mail) {
        return mailRepository.save(mail);
    }

    @Override
    public Mail get(@NonNull String id) {
        return mailRepository.findById(id).orElse(null);
    }

    @Override
    public String addToProcessing(@NonNull Mail mail, MultipartFile[] files) {
        mail.setDate(new Date());
        mail = save(mail);
        final String id = mail.getId();
        if (files.length != 0) {
            mail.setMultipart(true);
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    fileService.store(file, mail.getId());
                }
            }
        }
        if (mailRepository.countByStatus(MailStatus.EXPECTS) == 0 && queue.offer(id)) {
            mail.setStatus(MailStatus.PROCESSED);
            save(mail);
        } else {
            mail.setStatus(MailStatus.EXPECTS);
            save(mail);
            executorService.submit(Caretaker::new);
        }
        return id;
    }

    @Override
    public MailStatus getDeliveryStatus(String id) {
        return get(id).getStatus();
    }

    @Override
    public void send(Mail mail) {
        List<File> files = fileService.find(mail.getId());
        try {
            mailSender.send(MailUtils.toMimeMessage(mail, mailSender, files));
            LOG.info("Message was successfully sent!");
            mail.setStatus(MailStatus.OK);
        } catch (MessagingException e) {
            LOG.error("Message sending failed!", e);
            mail.setStatus(MailStatus.ERROR);
        }finally {
            save(mail);
            MailUtils.cleaning(files);
        }
    }

    private class Caretaker implements Runnable {

        @Override
        public void run() {
            for (int page = 0; mailRepository.countByStatus(MailStatus.EXPECTS) > 0; page++) {
                while (queue.size() > LIMIT) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        LOG.error("Thread was interrupted!", e);
                        return;
                    }
                }
                Pageable pageable = new PageRequest(page, LIMIT);
                List<Mail> mails = mailRepository.findByStatusOrderByDateAsc(MailStatus.EXPECTS, pageable);
                mails.forEach((m) -> {
                    try {
                        queue.put(m.getId());
                    } catch (InterruptedException e) {
                        LOG.error("Thread was interrupted!", e);
                        return;
                    }
                    m.setStatus(MailStatus.PROCESSED);
                    save(m);
                });
            }
        }
    }
}
