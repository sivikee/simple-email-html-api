package com.sivikee.email_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivikee.email_api.model.EmailRequest;
import com.sivikee.email_api.model.EmailResult;
import com.sivikee.email_api.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_KEY = "test-key";

    @Test
    void sendEmail_missingApiKey_returns401() throws Exception {
        EmailRequest request = EmailRequest.builder()
                .to("user@example.com")
                .subject("Hello")
                .body("World")
                .build();

        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendEmail_invalidEmail_returns400() throws Exception {
        EmailRequest request = EmailRequest.builder()
                .to("not-an-email")
                .subject("Hello")
                .body("World")
                .build();

        mockMvc.perform(post("/api/email")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendEmail_blankTo_returns400() throws Exception {
        EmailRequest request = EmailRequest.builder()
                .to("")
                .subject("Hello")
                .body("World")
                .build();

        mockMvc.perform(post("/api/email")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void webhookSend_invalidEmail_returns400() throws Exception {
        mockMvc.perform(get("/api/email/send")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .param("to", "not-an-email")
                        .param("subject", "Hello")
                        .param("body", "World"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendEmail_validRequest_returns200() throws Exception {
        when(emailService.sendEmail(any())).thenReturn(
                EmailResult.builder().message("Email sent successfully").status("SUCCESS").build()
        );

        EmailRequest request = EmailRequest.builder()
                .to("user@example.com")
                .subject("Hello")
                .body("World")
                .build();

        mockMvc.perform(post("/api/email")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
