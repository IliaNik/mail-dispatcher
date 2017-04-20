package com.mail.dispatcher.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author IliaNik on 19.04.2017.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Mail {
    @Id
    private Integer id;

    @Size(min=1, max=240, message = "Check message length")
    private String text;

    @Email
    @NotNull
    private String from;

    @Email(message = "Check email")
    @NotNull
    private String to;

    @Size(max=100, message = "Check subject")
    private String subject;
}
