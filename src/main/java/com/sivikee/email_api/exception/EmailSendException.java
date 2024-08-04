package com.sivikee.email_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailSendException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public EmailSendException(String message, HttpStatus status) {
        this.status = status;
        this.message = message;
    }

    public EmailSendException(String message) {
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
    }
}
