package com.sivikee.email_api.exception;

import com.sivikee.email_api.model.SimpleErrorDetail;
import com.sivikee.email_api.model.ValidationErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorDetail> handleNotValidInputException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorDetail.builder().message("Validation has failed on request").errors(errors).build());
    }

    @ExceptionHandler({EmailSendException.class})
    public ResponseEntity<SimpleErrorDetail> handleEmailSendException(EmailSendException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(SimpleErrorDetail.builder().error(exception.getClass().getSimpleName()).message(exception.getMessage()).build());
    }
}
