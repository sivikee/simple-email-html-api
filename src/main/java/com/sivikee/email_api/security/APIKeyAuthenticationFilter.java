package com.sivikee.email_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
@RequiredArgsConstructor
@Component

public class APIKeyAuthenticationFilter extends GenericFilterBean {

    private final APIKeyService apiKeyService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String method = httpRequest.getMethod();
            String apiKeyParam = httpRequest.getParameter("apiKey");

            Authentication authentication = null;

            // Accept API key from query param if GET request
            if ("GET".equalsIgnoreCase(method) && apiKeyParam != null) {
                authentication = apiKeyService.getAuthenticationFromKey(apiKeyParam);
            } else {
                authentication = apiKeyService.getAuthentication(httpRequest);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exp) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = httpResponse.getWriter();
            writer.print(exp.getMessage());
            writer.flush();
            writer.close();
            return; // Prevents further filter processing after failed auth
        }

        filterChain.doFilter(request, response);
    }
}
