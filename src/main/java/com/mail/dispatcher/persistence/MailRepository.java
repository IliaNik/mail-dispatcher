package com.mail.dispatcher.persistence;

import java.util.Optional;
import com.mail.dispatcher.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IliaNik on 19.04.2017.
 */
@Repository
public interface MailRepository extends MongoRepository<Mail, Integer> {
    Optional<Mail> findById(Integer id);
}
