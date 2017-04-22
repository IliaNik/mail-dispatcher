package com.mail.dispatcher.persistence;

import java.util.List;
import java.util.Optional;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IliaNik on 19.04.2017.
 */
@Repository
public interface MailRepository extends MongoRepository<Mail, Integer> {
    Optional<Mail> findById(Integer id);

    Integer countByStatus(MailStatus mailStatus);

    List<Mail> findByStatusOrderByDateAsc(MailStatus mailStatus, Pageable pageable);
}
