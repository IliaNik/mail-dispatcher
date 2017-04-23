package com.mail.dispatcher.dto.error;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationErrorDTO {

    private String message;
    private List<FieldErrorDTO> fieldErrors = new ArrayList<>();

    public void addFieldError(String code, String defaultMessage, String field) {
        fieldErrors.add(new FieldErrorDTO(code, defaultMessage, field));
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
