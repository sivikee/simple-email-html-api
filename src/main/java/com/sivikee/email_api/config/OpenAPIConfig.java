package com.sivikee.email_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String API_KEY_SCHEME = "ApiKeyAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simple Email HTML API")
                        .description("""
                                A lightweight REST API for sending styled HTML or plain text emails using \
                                Spring Boot, Thymeleaf, and Docker.
                                
                                ## Authentication
                                All endpoints require an API key supplied via the `X-API-KEY` request header,\
                                 or via the `apiKey` query parameter on GET requests.
                                
                                ## Templating
                                HTML email templates are resolved from the directory configured by \
                                `API_TEMPLATE_DIR` (mounted as a Docker volume). \
                                Templates use Thymeleaf syntax — pass dynamic values through the `data` map.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("sivikee")
                                .url("https://github.com/sivikee"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                                .description("API key passed in the `X-API-KEY` header. " +
                                        "For GET (webhook) requests you may also use the `apiKey` query parameter.")));
    }
}
