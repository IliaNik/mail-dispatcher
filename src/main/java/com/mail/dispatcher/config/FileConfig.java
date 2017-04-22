package com.mail.dispatcher.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * @author IliaNik on 22.04.2017.
 */
@Configuration
public class FileConfig {

    @Autowired
    MongoDbFactory mongoDbFactory;

    @Autowired
    MappingMongoConverter mappingMongoConverter;


    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory, mappingMongoConverter);
    }
}
