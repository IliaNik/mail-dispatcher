package com.mail.dispatcher.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.util.MailStatus;
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

    @Autowired
    private JavaMailSender mailSender;

    private MimeMessage toMimeMessage(final Mail mail) throws MessagingException {
        final MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage());
        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getText(), true);
        return helper.getMimeMessage();
    }

    @Override
    public Integer send(Mail mail) {
        return null;
    }

    @Override
    public MailStatus checkDeliveryStatus(Integer id) {
        return null;
    }
}
