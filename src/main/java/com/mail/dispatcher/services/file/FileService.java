package com.mail.dispatcher.services.file;

import java.io.File;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 22.04.2017.
 */
public interface FileService {

    String store(final MultipartFile file, final String messageId);

    List<File> find(String messageId);

}
