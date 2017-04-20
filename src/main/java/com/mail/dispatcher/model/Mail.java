package com.mail.dispatcher.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
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

    private Integer status;

    @Length.List({
            @Length(min = 1, message = "The text must be at least 1 character"),
            @Length(max = 300, message = "The text must be less than 300 characters")
    })
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
