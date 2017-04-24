package com.mail.dispatcher.web;

import java.util.Optional;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import com.mail.dispatcher.services.mail.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * @author IliaNik on 24.04.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MailRestControllerTest {

    private final static String ID = "fakeid";

    @Mock
    MailService mailService;

    @InjectMocks
    MailRestController sut;

    @Test
    public void sendMail() throws Exception {
        final Mail mail = new Mail();
        mail.setTo("example@gmail.com");
        mail.setSubject("subject");
        mail.setText("Text");

        final MultipartFile file[] = new MultipartFile[0];
        final ResponseEntity<?> responseEntity = sut.sendMail(mail, file);

        verify(mailService).addToProcessing(mail, file);
    }

    @Test
    public void getDeliveryStatus() throws Exception {
        when(mailService.getDeliveryStatus(any(String.class))).thenReturn(MailStatus.PROCESSED);

        final ResponseEntity<?> responseEntity = sut.getDeliveryStatus(ID);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void getDeliveryStatusWait() throws Exception {

        when(mailService.getDeliveryStatus(any(String.class))).thenReturn(MailStatus.EXPECTS);
        final ResponseEntity<?> responseEntity = sut.getDeliveryStatus(ID);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void getDeliveryStatusOK() throws Exception {
        when(mailService.getDeliveryStatus(any(String.class))).thenReturn(MailStatus.OK);
        final ResponseEntity<?> responseEntity = sut.getDeliveryStatus(ID);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getDeliveryStatusError() throws Exception {
        when(mailService.getDeliveryStatus(any(String.class))).thenReturn(MailStatus.ERROR);
        final ResponseEntity<?> responseEntity = sut.getDeliveryStatus(ID);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

}