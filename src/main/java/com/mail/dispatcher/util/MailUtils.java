package com.mail.dispatcher.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.io.Files;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.services.file.FileService;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author IliaNik on 22.04.2017.
 */
public class MailUtils {

    @Value("spring.mail.username")
    private static String FROM;

    public static MimeMessage toMimeMessage(final Mail mail, JavaMailSender mailSender,
                                            List<File> files) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(FROM);
        message.setRecipients(Message.RecipientType.TO, mail.getTo());
        message.setSubject(mail.getSubject());

        Multipart multipart = new MimeMultipart("mixed");

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(mail.getText());
        multipart.addBodyPart(bodyPart);

        for (File file : files) {
            bodyPart = new MimeBodyPart();
            try {
                bodyPart.attachFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            multipart.addBodyPart(bodyPart);
        }

        message.setContent(multipart);
        return message;
    }

    public static void cleaning(List<File> files) {
        files.forEach((p) -> {
            p.delete();
        });
    }
}
