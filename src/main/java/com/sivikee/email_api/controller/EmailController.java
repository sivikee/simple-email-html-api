package com.sivikee.email_api.controller;

import com.sivikee.email_api.model.EmailRequest;
import com.sivikee.email_api.model.EmailResult;
import com.sivikee.email_api.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequiredArgsConstructor
@RequestMapping("api/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping()
    public ResponseEntity<EmailResult> sendEmail(@RequestBody @Valid EmailRequest request) {
        EmailResult result =  emailService.sendEmail(request);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/render")
    public ResponseEntity<String> render(@RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(emailService.generateTemplate(request));
    }

    @GetMapping("/send")
    public ResponseEntity<EmailResult> sendEmailWebhook(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        EmailResult result =  emailService.sendEmail(EmailRequest.builder().to(to).body(body).subject(subject).build());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

}
