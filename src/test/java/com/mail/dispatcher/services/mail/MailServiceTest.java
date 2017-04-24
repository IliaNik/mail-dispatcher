package com.mail.dispatcher.services.mail;

import javax.mail.Message;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.mail.dispatcher.model.Mail;
import com.mail.dispatcher.model.MailStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author IliaNik on 24.04.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceTest {

    private final static String ID = "testid";

    @Autowired
    private MailService sut;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static GreenMail testSmtp;

    private Mail mail;

    @BeforeClass
    public static void testSmtpInit() {
        testSmtp = new GreenMail(ServerSetupTest.SMTP);
    }

    @AfterClass
    public static void cleanup() {
        testSmtp.stop();
    }

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(Mail.class);
        mail = new Mail();
        mail.setTo("example@gmail.com");
        mail.setSubject("subject");
        mail.setText("Text");
        mail.setId(ID);
        mail.setStatus(MailStatus.OK);
        mongoTemplate.save(mail);
    }

    @Test
    public void save() throws Exception {
        mail.setTo("another@gmail.com");
        mail.setSubject("another subject");
        mail.setText("another Text");

        mail = sut.save(mail);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(mail.getId()));
        Mail resultMail = mongoTemplate.findOne(query, Mail.class);

        assertEquals(mail, resultMail);
    }

    @Test
    public void get() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(ID));
        mail = mongoTemplate.findOne(query, Mail.class);
        Mail resultMail = sut.get(ID);

        assertEquals(mail, resultMail);
        assertNull(sut.get("Donotexist"));
    }

    @Test
    public void addToProcessing() throws Exception {
        MultipartFile[] files = {};
        String id = sut.addToProcessing(mail, files);
        mail = sut.get(id);
        assertEquals(mail.getStatus(), MailStatus.PROCESSED);
    }

    @Test
    public void getDeliveryStatus() throws Exception {
        String id = mail.getId();
        MailStatus status = sut.getDeliveryStatus(id);

        assertEquals(mail.getStatus(), status);
    }

    @Test
    public void send() throws Exception {
        testSmtp.reset();

        sut.send(mail);

        Message[] messages = testSmtp.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("subject", messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]);
        assertTrue(body.contains(mail.getText()));
    }

}