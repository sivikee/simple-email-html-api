package com.sivikee.email_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@Schema(description = "Request payload for sending an email")
public class EmailRequest {

    @Email
    @NotNull
    @Schema(description = "Recipient email address", example = "recipient@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String to;

    @NotBlank
    @Schema(description = "Subject line of the email", example = "Hello from Simple Email API", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;

    @Schema(description = "Plain-text body of the email. Required when `template` is not provided.", example = "Hello, this is a test email.")
    private String body;

    @Schema(description = "Thymeleaf template filename (without `.html` extension) resolved from the configured template directory. Required when `body` is not provided.", example = "welcome")
    private String template;

    @Schema(description = "Key-value pairs injected as template variables when using a Thymeleaf template.", example = "{\"name\": \"Alice\", \"link\": \"https://example.com\"}")
    private Map<String, Object> data;
}
