package com.sivikee.email_api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


import java.util.Map;

@Data
@Builder
public class EmailRequest {
    @Email
    @NotNull
    private String to;
    @NotBlank
    private String subject;
    private String body;
    private String template;
    private Map<String, Object> data;
}
