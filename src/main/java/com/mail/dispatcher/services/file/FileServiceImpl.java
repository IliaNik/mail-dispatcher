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

    public static final int CHUNK_SIZE = 500_000;
    public static final int DEFAULT_CHUNK_ID = 0;
    private String attachmentsDirectory = "src/main/resources/temporary/";
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
                files.add(saveTrack(in, file.getFilename()).toFile());
            } catch (IOException e) {
            }
        }
        return files;
    }

    private Path saveTrack(final InputStream stream, final String fileName) throws IOException {
        final Path path = Paths.get(attachmentsDirectory);
        File file = new File(attachmentsDirectory + fileName);
        final Path tmp = Paths.get(file.getAbsolutePath());
        try (BufferedInputStream in = new BufferedInputStream(stream);
             OutputStream out = newOutputStream(tmp)) {

            IOUtils.copy(in, out);
            splitFile(tmp, path);
        }
        return tmp;
    }

    private void splitFile(final Path filePath, final Path dirPath) throws IOException {
        int partCounter = DEFAULT_CHUNK_ID;
        final byte[] buffer = new byte[CHUNK_SIZE];
        try (BufferedInputStream bis = new BufferedInputStream(newInputStream(filePath))) {
            int length = bis.read(buffer);
            while (length > 0) {
                final Path chunkPath = dirPath.resolve(createChunkName(partCounter++));
                try (OutputStream out = newOutputStream(chunkPath)) {
                    out.write(buffer, 0, length);
                }
                length = bis.read(buffer);
            }
        }
    }

    private String createChunkName(int counter) {
        return String.format("%04d", counter);
    }
}
