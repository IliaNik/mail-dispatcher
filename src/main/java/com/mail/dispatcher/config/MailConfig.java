package com.mail.dispatcher.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * @author IliaNik on 16.08.2016.
 */
@Configuration
public class MailConfig {

    private final static int PORT = 25;

    @Value("${spring.mail.host}")
    private String HOST;

    @Value("${spring.mail.username}")
    private String USERNAME;

    @Value("${spring.mail.password}")
    private String PASSWORD;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String PROTOCOL;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String AUTH;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String STARTTLS;

    @Value("${spring.mail.properties.mail.debug")
    private String DEBUG;

    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(HOST);
        javaMailSender.setPort(PORT);
        javaMailSender.setUsername(USERNAME);
        javaMailSender.setPassword(PASSWORD);

        final Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.transport.protocol", PROTOCOL);
        javaMailProperties.setProperty("mail.smtp.auth", AUTH);
        javaMailProperties.setProperty("mail.smtp.starttls.enable", STARTTLS);
        javaMailProperties.setProperty("mail.debug", DEBUG);
        javaMailSender.setJavaMailProperties(javaMailProperties);

        return javaMailSender;
    }
}
