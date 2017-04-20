package com.mail.dispatcher.services;

import com.mail.dispatcher.model.Mail;

import com.mail.dispatcher.util.MailStatus;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author IliaNik on 20.04.2017.
 */

@Service
public interface MailService {

    Integer send(@NonNull Mail mail);
    MailStatus checkDeliveryStatus(@NonNull Integer id);
}
