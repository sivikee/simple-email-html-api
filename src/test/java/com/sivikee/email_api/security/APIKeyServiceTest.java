package com.sivikee.email_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class APIKeyServiceTest {

    private APIKeyService apiKeyService;

    @BeforeEach
    void setUp() throws Exception {
        apiKeyService = new APIKeyService();
        var field = APIKeyService.class.getDeclaredField("key");
        field.setAccessible(true);
        field.set(apiKeyService, "test-secret-key");
    }

    @Test
    void validKey_returnsAuthentication() {
        Authentication auth = apiKeyService.getAuthenticationFromKey("test-secret-key");
        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getPrincipal()).isEqualTo("test-secret-key");
    }

    @Test
    void invalidKey_throwsBadCredentials() {
        assertThatThrownBy(() -> apiKeyService.getAuthenticationFromKey("wrong-key"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid API Key");
    }

    @Test
    void nullKey_throwsBadCredentials() {
        assertThatThrownBy(() -> apiKeyService.getAuthenticationFromKey(null))
                .isInstanceOf(BadCredentialsException.class);
    }
}
