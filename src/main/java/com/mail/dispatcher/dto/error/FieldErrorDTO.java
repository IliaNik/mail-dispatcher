package com.mail.dispatcher.dto.error;

import java.io.Serializable;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FieldErrorDTO implements Serializable {

    @NonNull
    private String code;
    @NonNull
    private String message;
    @NonNull
    private String field;
    
}
