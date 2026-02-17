package com.sivikee.email_api.service;

import com.sivikee.email_api.exception.EmailSendException;
import com.sivikee.email_api.model.EmailRequest;
import com.sivikee.email_api.model.EmailResult;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    /**
     * Send a plain-text or HTML (Thymeleaf-templated) email.
     *
     * @param request email request containing recipient, subject, and either a body or template name + data
     * @return result of the send operation
     */
    public EmailResult sendEmail(EmailRequest request) {
        return sendEmailWithAttachments(request, null);
    }

    /**
     * Send an email with optional file attachments.
     *
     * @param request email request containing recipient, subject, and either a body or template name + data
     * @param files   optional list of files to attach; may be {@code null} or empty
     * @return result of the send operation
     */
    public EmailResult sendEmailWithAttachments(EmailRequest request, List<MultipartFile> files) {
        if (request.getBody() == null && request.getTemplate() == null) {
            throw new EmailSendException("The request body or template must be filled!");
        }
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper mailMessage = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            if (request.getTemplate() != null) {
                mailMessage.setText(generateTemplate(request), true);
            } else {
                mailMessage.setText(request.getBody());
            }

            mailMessage.setFrom(sender);
            mailMessage.setTo(request.getTo());
            mailMessage.setSubject(request.getSubject());

            if (files != null) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        mailMessage.addAttachment(
                                Objects.requireNonNullElse(file.getOriginalFilename(), "attachment"),
                                file
                        );
                    }
                }
            }

            mailSender.send(mimeMessage);
            return EmailResult.builder()
                    .message("Email sent successfully")
                    .status("SUCCESS")
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (EmailSendException e) {
            throw e;
        } catch (Exception e) {
            return EmailResult.builder()
                    .message(e.getMessage())
                    .status("FAILED")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Render the Thymeleaf template specified in the request using the provided data variables.
     *
     * @param request email request containing the template name and data map
     * @return rendered HTML string
     * @throws EmailSendException if the template is not found or cannot be processed
     */
    public String generateTemplate(EmailRequest request) {
        Context context = new Context();
        if (request.getData() != null) {
            request.getData().forEach(context::setVariable);
        }
        try {
            return this.templateEngine.process(request.getTemplate(), context);
        } catch (TemplateInputException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                throw new EmailSendException(
                        String.format("Template file not found: %s", request.getTemplate()),
                        HttpStatus.BAD_REQUEST);
            }
            throw new EmailSendException(
                    String.format("Error processing template: %s", request.getTemplate()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new EmailSendException(
                    String.format("Template not found or invalid: %s", request.getTemplate()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}

