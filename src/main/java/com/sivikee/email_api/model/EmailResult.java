package com.sivikee.email_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Result of an email send operation")
public class EmailResult {

    @Schema(description = "Human-readable result message", example = "Email sent successfully")
    private String message;

    @Schema(description = "Operation status", example = "SUCCESS", allowableValues = {"SUCCESS"})
    private String status;
}
