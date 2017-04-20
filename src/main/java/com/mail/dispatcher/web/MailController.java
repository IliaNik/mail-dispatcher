package com.mail.dispatcher.web;

import javax.validation.Valid;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.services.MailService;
import com.mail.dispatcher.util.MailStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author IliaNik on 19.04.2017.
 */
@RestController
@RequestMapping("/mail")
public class MailController {
    private static final Logger LOG = LoggerFactory.getLogger(MailController.class);

    @Autowired
    MailService mailService;

    @RequestMapping(method = POST)
    public Integer sendMail(@Valid @RequestBody Mail mail){
        return mailService.send(mail);
    }

    @RequestMapping(method = GET)
    public ResponseEntity<?> getDeliveryStatus(Integer id){
        if(id == null){
            return new ResponseEntity<>(
                    "Id mustn't be null",
                    HttpStatus.BAD_REQUEST);
        }
        MailStatus mailStatus = mailService.checkDeliveryStatus(id);

        switch(mailStatus){
            case EXPECTS: return new ResponseEntity<>("Message hasn't been delivered yet ", HttpStatus.PROCESSING);
            case OK: return new ResponseEntity<>("The message was delivered successfully", HttpStatus.OK);
            case ERROR: return new ResponseEntity<>("The message can't be sent", HttpStatus.FORBIDDEN);
            default: {
                LOG.error("Unknown status");
                return new ResponseEntity<>("Unknown status", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
