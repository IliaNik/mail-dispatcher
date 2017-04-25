package com.mail.dispatcher.services.file;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.NonNull;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.Files.*;
import static java.util.Objects.requireNonNull;

/**
 * @author IliaNik on 22.04.2017.
 */
@Service("fileService")
@Transactional
public class FileServiceImpl implements FileService {

    @Value("${app.attachmentsDirectory}")
    private String attachmentsDirectory;
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
    public List<File> find(String messageId) {
        List<GridFSDBFile> fsdbFiles = gridFsTemplate.find(new Query(Criteria.where("metadata.messageId").is(messageId)));
        List<File> files = new ArrayList<>();
        for (GridFSDBFile file : fsdbFiles) {
            Path path = null;
            try (InputStream in = file.getInputStream()) {
                files.add(saveFile(in, file.getFilename()).toFile());
            } catch (IOException e) {
            }
        }
        return files;
    }

    private Path saveFile(final InputStream stream, final String fileName) throws IOException {
        final Path path = Paths.get(attachmentsDirectory);
        File file = new File(attachmentsDirectory + fileName);
        final Path tmp = Paths.get(file.getAbsolutePath());
        try (BufferedInputStream in = new BufferedInputStream(stream);
             OutputStream out = newOutputStream(tmp)) {
            IOUtils.copy(in, out);
            out.flush();
        }
        return tmp;
    }
}
