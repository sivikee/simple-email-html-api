package com.sivikee.email_api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleErrorDetail {
    private String error;
    private String message;
}
