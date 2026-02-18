package com.sivikee.email_api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class APIKeyService {
    @Value("${api.key}")
    private String key;
    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        return getAuthenticationFromKey(apiKey);
    }

    public Authentication getAuthenticationFromKey(String apiKey) {
        if (!isValidKey(apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }
        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }

    /**
     * Constant-time equality check to prevent timing attacks.
     */
    private boolean isValidKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                key.getBytes(StandardCharsets.UTF_8)
        );
    }
}
