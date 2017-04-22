package com.mail.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@SpringBootApplication
public class MailDispatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailDispatcherApplication.class, args);
	}

}
