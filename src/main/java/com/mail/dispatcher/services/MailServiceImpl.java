package com.mail.dispatcher.services;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.*;
import com.google.common.collect.ImmutableList;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.persistence.MailRepository;
import com.mail.dispatcher.util.MailStatus;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author IliaNik on 20.04.2017.
 */
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final Integer CAPASITY = 300;
    private static final Integer LIMIT = CAPASITY / 2;

    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CAPASITY);
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private JavaMailSender mailSender;

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
        mail.setStatus(MailStatus.EXPECTS);
        mail = save(mail);
        final Integer id = mail.getId();

        if (queue.offer(id) && mailRepository.countByStatus(MailStatus.EXPECTS) == 0) {
            mail.setStatus(MailStatus.PROCESSED);
        } else {
            mail.setStatus(MailStatus.EXPECTS);
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
            mail.setStatus(MailStatus.OK);
        } catch (MessagingException e) {
            LOG.error("Message sending failed!", e);
            mail.setStatus(MailStatus.ERROR);
        }
        save(mail);
    }


    private MimeMessage toMimeMessage(final Mail mail) throws MessagingException {
        final MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage());
        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getText(), true);
        return helper.getMimeMessage();
    }

    private class Caretaker implements Runnable {

        @Override
        public void run() {
            Integer page = 0;
            while (mailRepository.countByStatus(MailStatus.EXPECTS) > 0) {
                while (queue.size() > LIMIT) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                Pageable pageable = new PageRequest(page, LIMIT);
                List<Mail> mails = mailRepository.findByStatusOrderByDateAsc(MailStatus.EXPECTS, pageable);
                if (!mails.isEmpty()) {
                    mails.forEach((m) -> {
                        try {
                            queue.put(m.getId());
                        } catch (InterruptedException e) {
                            return;
                        }
                        m.setStatus(MailStatus.PROCESSED);
                        save(m);
                    });
                } else {
                    return;
                }
                page++;
            }
        }
    }
}
