package com.mail.dispatcher.web.advice;

import java.util.List;
import com.mail.dispatcher.dto.error.ValidationErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author IliaNik on 25.03.2017.
 */
@ControllerAdvice
public class RestErrorHandler {


//    TODO: internationalization
//    @Autowired
//    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return processFieldErrors(fieldErrors);
    }

    private ValidationErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
        ValidationErrorDTO dto = new ValidationErrorDTO();
        dto.setMessage("Bad fields");

        for (FieldError fieldError : fieldErrors) {

            //String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            dto.addFieldError(fieldError.getCode(), fieldError.getDefaultMessage(), fieldError.getField());
        }

        return dto;
    }

//    private String resolveLocalizedErrorMessage(FieldError fieldError) {
//        Locale currentLocale =  LocaleContextHolder.getLocale();
//        return messageSource.getMessage(fieldError, currentLocale);
//    }
}
