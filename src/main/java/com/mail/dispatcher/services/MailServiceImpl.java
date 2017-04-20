package com.mail.dispatcher.services;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.persistence.MailRepository;
import com.mail.dispatcher.util.Expectant;
import com.mail.dispatcher.util.MailStatus;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author IliaNik on 20.04.2017.
 */
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final Integer CAPASITY = 300;

    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CAPASITY);
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostConstruct
    private void queueProcessing(){

        executorService.submit(() -> {
            while(true) {
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
    public void addToProcessing(@NonNull Integer id) {
        if(!queue.offer(id)){
            executorService.submit(Expectant::new);
        }
    }

    @Override
    public MailStatus checkDeliveryStatus(Integer id) {
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

}
