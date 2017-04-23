package com.mail.dispatcher.web;

import javax.validation.Valid;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import com.mail.dispatcher.services.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 19.04.2017.
 */
@RestController
public class MailRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MailRestController.class);

    @Autowired
    MailService mailService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<?> sendMail(
            @Valid @RequestPart("mail") Mail mail,
            @RequestPart("uploadingFiles[]") MultipartFile[] files) {
        final String id = mailService.addToProcessing(mail, files);
        LOG.info("Mail with id {} added to processing", id);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDeliveryStatus(@PathVariable("id") String id) {
        if (id == null) {
            LOG.error("Bad request");
            return new ResponseEntity<>(
                    "Id mustn't be null",
                    HttpStatus.BAD_REQUEST);
        }
        final MailStatus mailStatus = mailService.getDeliveryStatus(id);

        switch (mailStatus) {
            case EXPECTS:
            case PROCESSED: {
                LOG.debug("The message with id {} hasn't been delivered yet", id);
                return new ResponseEntity<>("The message hasn't been delivered yet ", HttpStatus.NO_CONTENT);
            }
            case OK: {
                LOG.info("Message with id {} was delivered successfully", id);
                return new ResponseEntity<>("The message was delivered successfully", HttpStatus.OK);
            }
            case ERROR: {
                LOG.error("The message with id {} can't be sent", id);
                return new ResponseEntity<>("The message can't be sent", HttpStatus.FORBIDDEN);
            }
            default: {
                LOG.error("Unknown status");
                return new ResponseEntity<>("Unknown status", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
