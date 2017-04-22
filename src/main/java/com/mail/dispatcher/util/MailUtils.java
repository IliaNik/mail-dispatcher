package com.mail.dispatcher.util;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.services.file.FileService;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author IliaNik on 22.04.2017.
 */
public class MailUtils {

    @Autowired
    private static JavaMailSender mailSender;

    @Autowired
    private static FileService fileService;

    public static MimeMessage toMimeMessage(final Mail mail) throws MessagingException {
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
}
