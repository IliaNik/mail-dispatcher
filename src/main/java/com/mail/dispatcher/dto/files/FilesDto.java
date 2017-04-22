package com.mail.dispatcher.dto.files;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IliaNik on 22.04.2017.
 */
@Data
public class FilesDto {
    List<MultipartFile> files;
}
