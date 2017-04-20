package com.mail.dispatcher.dto.error;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ValidationErrorDTO {

    @NonNull
    private String code;
    @NonNull
    private String message;
    @NonNull
    private List<FieldErrorDTO> fieldErrors;

    public void addFieldError(String code, String defaultMessage, String field) {
        fieldErrors.add(new FieldErrorDTO(code, defaultMessage, field));
    }
}
