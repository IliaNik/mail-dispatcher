package com.mail.dispatcher.services.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 22.04.2017.
 */
@Service("fileService")
@Transactional
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);
    private static final String EMPTY_STRING = "";

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Override
    public String store(@NonNull MultipartFile file, @NonNull String messageId) {
        LOG.info("store file from multipart {}", file);

        DBObject metaData = new BasicDBObject();
        metaData.put("messageId", messageId);

        try (InputStream in = file.getInputStream()) {
            return gridFsTemplate.store(in, file.getOriginalFilename(), file.getContentType(), metaData)
                    .getId().toString();
        } catch (IOException e) {
            LOG.error("Exception during file saving!", e);
        }
        return EMPTY_STRING;
    }

    @Override
    public List<GridFSDBFile> find(String messageId) {
        return gridFsTemplate.find(new Query(Criteria.where("metadata.messageId").is(messageId)));
    }
}
