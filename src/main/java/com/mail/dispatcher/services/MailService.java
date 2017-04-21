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

    Mail save(@NonNull Mail mail);

    Mail get(@NonNull Integer id);

    Integer addToProcessing(@NonNull Mail mail);

    MailStatus getDeliveryStatus(@NonNull Integer id);

    void send(@NonNull Mail mail);
}
