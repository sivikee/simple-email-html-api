package com.sivikee.email_api.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;

@Configuration
@Slf4j
public class ThymeleafConfig implements WebMvcConfigurer {

    @Value("${api.template-dir}")
    private String thymeleafPrefix;

    @Value("${api.template-cache-enabled:true}")
    private boolean templateCacheEnabled;
    @PostConstruct
    public void createTemplatesDirectory() {
        String directoryPath = thymeleafPrefix.replace("file:", "");

        File templatesDir = new File(directoryPath);
        if (!templatesDir.exists()) {
            log.info("Thymeleaf template folder not exists, creating it now!");
            templatesDir.mkdirs();
        }
    }

    @Bean
    public FileTemplateResolver templateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();

        resolver.setPrefix(thymeleafPrefix);
        resolver.setCacheable(templateCacheEnabled);
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());

        return engine;
    }
}