package com.sivikee.email_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Simple error detail returned for application-level errors")
public class SimpleErrorDetail {

    @Schema(description = "Exception class name", example = "EmailSendException")
    private String error;

    @Schema(description = "Error message", example = "Template file not found: welcome")
    private String message;
}
