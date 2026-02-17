package com.sivikee.email_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
@Schema(description = "Validation error detail returned when request fields fail validation")
public class ValidationErrorDetail {

    @Schema(description = "Generic validation failure message", example = "Validation has failed on request")
    private String message;

    @Schema(description = "Map of field names to their validation error messages", example = "{\"to\": \"must be a well-formed email address\"}")
    private Map<String, String> errors;
}
