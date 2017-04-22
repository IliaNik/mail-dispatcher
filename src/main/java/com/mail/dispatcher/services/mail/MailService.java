package com.mail.dispatcher.services.mail;

import com.mail.dispatcher.dto.files.FilesDto;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author IliaNik on 20.04.2017.
 */

@Service
public interface MailService {

    Mail save(@NonNull Mail mail);

    Mail get(@NonNull Integer id);

    Integer addToProcessing(@NonNull Mail mail, FilesDto filesDto);

    MailStatus getDeliveryStatus(@NonNull Integer id);

    void send(@NonNull Mail mail);
}
