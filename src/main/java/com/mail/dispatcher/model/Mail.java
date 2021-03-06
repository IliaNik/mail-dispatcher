package com.mail.dispatcher.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author IliaNik on 19.04.2017.
 */

@Data
@NoArgsConstructor
@Document
public class Mail {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Indexed
    private MailStatus status;

    @Length.List({
            @Length(min = 1, message = "The text must be at least 1 character"),
            @Length(max = 300, message = "The text must be less than 300 characters")
    })
    private String text;

    @Email(message = "Check email")
    @NotNull
    private String to;

    @Size(max=100, message = "Subject too long")
    @NotNull
    private String subject;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean multipart;
}
