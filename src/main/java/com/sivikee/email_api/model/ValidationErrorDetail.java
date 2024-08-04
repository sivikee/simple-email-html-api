package com.sivikee.email_api.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ValidationErrorDetail {
    private String message;
    private Map<String,String> errors;
}
