package com.sivikee.email_api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class APIKeyService {
    @Value("${api.key}")
    private String key;
    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || !apiKey.equals(key)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
