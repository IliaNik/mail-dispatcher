package com.mail.dispatcher.services.file;

import java.util.List;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 22.04.2017.
 */
public interface FileService {

    String store(final MultipartFile file, final String messageId);

    List<GridFSDBFile> find(String messageId);

}
