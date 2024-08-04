package com.sivikee.email_api.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class EmailResult {
    private String message;
    private String status;
    private HttpStatus httpStatus;
}
