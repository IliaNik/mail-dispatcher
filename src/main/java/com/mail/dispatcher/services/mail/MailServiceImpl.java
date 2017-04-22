package com.mail.dispatcher.services.mail;

import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.concurrent.*;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.persistence.MailRepository;
import com.mail.dispatcher.model.MailStatus;
import com.mail.dispatcher.services.file.FileService;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author IliaNik on 20.04.2017.
 */
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final Integer CAPASITY = 100000;
    private static final Integer LIMIT = CAPASITY / 10;

    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CAPASITY);
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
            while (true) {
                Integer id = queue.take();
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
    public Mail get(@NonNull Integer id) {
        return mailRepository.findById(id).orElse(null);
    }

    @Override
    public Integer addToProcessing(@NonNull Mail mail) {
        mail = save(mail);
        final Integer id = mail.getId();

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
    public MailStatus getDeliveryStatus(Integer id) {
        return get(id).getStatus();
    }

    @Override
    public void send(Mail mail) {
        try {
            mailSender.send(toMimeMessage(mail));
            LOG.info("Message was successfully sent!");
            mail.setStatus(MailStatus.OK);
        } catch (MessagingException e) {
            LOG.error("Message sending failed!", e);
            mail.setStatus(MailStatus.ERROR);
        }
        save(mail);
    }


    private MimeMessage toMimeMessage(final Mail mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(mail.getFrom());
        message.setRecipients(Message.RecipientType.TO, mail.getTo());
        message.setSubject(mail.getSubject());

        Multipart multipart = new MimeMultipart();

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(mail.getText());
        multipart.addBodyPart(bodyPart);

        if (mail.isMultipart()) {
            List<GridFSDBFile> files = fileService.find(mail.getId());
            for (GridFSDBFile file : files) {
                bodyPart = new MimeBodyPart(file.getInputStream());
                bodyPart.setHeader("Content-Type", file.getContentType());
                multipart.addBodyPart(bodyPart);
            }
        }
        message.setContent(multipart);
        return message;
    }

    private class Caretaker implements Runnable {

        @Override
        public void run() {
            for (int page = 0; mailRepository.countByStatus(MailStatus.EXPECTS) > 0; page++) {
                while (queue.size() > LIMIT) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                Pageable pageable = new PageRequest(page, LIMIT);
                List<Mail> mails = mailRepository.findByStatusOrderByDateAsc(MailStatus.EXPECTS, pageable);
                mails.forEach((m) -> {
                    try {
                        queue.put(m.getId());
                    } catch (InterruptedException e) {
                        return;
                    }
                    m.setStatus(MailStatus.PROCESSED);
                    save(m);
                });
            }
        }
    }
}
