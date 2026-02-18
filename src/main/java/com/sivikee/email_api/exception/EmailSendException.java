package com.sivikee.email_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailSendException extends RuntimeException {
    private final HttpStatus status;

    public EmailSendException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public EmailSendException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
