package com.sivikee.email_api.controller;

import com.sivikee.email_api.model.EmailRequest;
import com.sivikee.email_api.model.EmailResult;
import com.sivikee.email_api.model.SimpleErrorDetail;
import com.sivikee.email_api.model.ValidationErrorDetail;
import com.sivikee.email_api.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("api/email")
@Tag(name = "Email", description = "Endpoints for sending and previewing emails")
@SecurityRequirement(name = "ApiKeyAuth")
public class EmailController {

    private final EmailService emailService;

    @PostMapping()
    @Operation(
            summary = "Send an email",
            description = "Send a plain-text or Thymeleaf-templated HTML email. " +
                    "Provide either `body` (plain text) or `template` + `data` (HTML template).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email sent successfully",
                            content = @Content(schema = @Schema(implementation = EmailResult.class))),
                    @ApiResponse(responseCode = "400", description = "Validation or template error",
                            content = @Content(schema = @Schema(oneOf = {ValidationErrorDetail.class, SimpleErrorDetail.class}))),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid API key"),
                    @ApiResponse(responseCode = "500", description = "Mail server error",
                            content = @Content(schema = @Schema(implementation = EmailResult.class)))
            }
    )
    public ResponseEntity<EmailResult> sendEmail(@RequestBody @Valid EmailRequest request) {
        EmailResult result = emailService.sendEmail(request);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping(value = "/attach", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Send an email with file attachments",
            description = "Send an email with one or more file attachments via `multipart/form-data`. " +
                    "The `request` part must be a JSON object with the same fields as the standard send-email endpoint. " +
                    "Attach files using the `files` part (can be repeated for multiple files).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email sent successfully",
                            content = @Content(schema = @Schema(implementation = EmailResult.class))),
                    @ApiResponse(responseCode = "400", description = "Validation or template error",
                            content = @Content(schema = @Schema(oneOf = {ValidationErrorDetail.class, SimpleErrorDetail.class}))),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid API key"),
                    @ApiResponse(responseCode = "500", description = "Mail server error",
                            content = @Content(schema = @Schema(implementation = EmailResult.class)))
            }
    )
    public ResponseEntity<EmailResult> sendEmailWithAttachments(
            @RequestPart("request") @Valid EmailRequest request,
            @RequestPart(value = "files", required = false)
            @Parameter(description = "Files to attach to the email") List<MultipartFile> files) {
        EmailResult result = emailService.sendEmailWithAttachments(request, files);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/render")
    @Operation(
            summary = "Preview a rendered email template",
            description = "Returns the rendered HTML content of the given Thymeleaf template without sending " +
                    "the email. Useful for verifying template output before sending.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rendered HTML returned"),
                    @ApiResponse(responseCode = "400", description = "Template not found or invalid",
                            content = @Content(schema = @Schema(implementation = SimpleErrorDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid API key")
            }
    )
    public ResponseEntity<String> render(@RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(emailService.generateTemplate(request));
    }

    @GetMapping("/send")
    @Operation(
            summary = "Send a plain-text email via webhook",
            description = "Quick webhook endpoint for sending plain-text emails using query parameters. " +
                    "Authenticate by passing the `apiKey` query parameter instead of the `X-API-KEY` header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email sent successfully",
                            content = @Content(schema = @Schema(implementation = EmailResult.class))),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid API key"),
                    @ApiResponse(responseCode = "500", description = "Mail server error",
                            content = @Content(schema = @Schema(implementation = EmailResult.class)))
            }
    )
    public ResponseEntity<EmailResult> sendEmailWebhook(
            @RequestParam @Parameter(description = "Recipient email address", example = "recipient@example.com") String to,
            @RequestParam @Parameter(description = "Email subject", example = "Hello!") String subject,
            @RequestParam @Parameter(description = "Plain-text email body", example = "This is a webhook email.") String body) {
        EmailResult result = emailService.sendEmail(EmailRequest.builder().to(to).body(body).subject(subject).build());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}

