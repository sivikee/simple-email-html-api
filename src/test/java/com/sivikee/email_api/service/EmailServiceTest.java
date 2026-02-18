package com.sivikee.email_api.service;

import com.sivikee.email_api.exception.EmailSendException;
import com.sivikee.email_api.model.EmailRequest;
import com.sivikee.email_api.model.EmailResult;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "sender", "sender@example.com");
    }

    @Test
    void sendEmail_withBody_succeeds() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        EmailRequest request = EmailRequest.builder()
                .to("recipient@example.com")
                .subject("Test")
                .body("Hello!")
                .build();

        EmailResult result = emailService.sendEmail(request);

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getMessage()).isEqualTo("Email sent successfully");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_noBodyNoTemplate_throwsEmailSendException() {
        EmailRequest request = EmailRequest.builder()
                .to("recipient@example.com")
                .subject("Test")
                .build();

        assertThatThrownBy(() -> emailService.sendEmail(request))
                .isInstanceOf(EmailSendException.class)
                .hasMessageContaining("body or template");
    }

    @Test
    void sendEmail_mailServerThrows_throwsEmailSendException() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Connection refused")).when(mailSender).send(any(MimeMessage.class));

        EmailRequest request = EmailRequest.builder()
                .to("recipient@example.com")
                .subject("Test")
                .body("Hello!")
                .build();

        assertThatThrownBy(() -> emailService.sendEmail(request))
                .isInstanceOf(EmailSendException.class)
                .satisfies(ex -> {
                    assertThat(((EmailSendException) ex).getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    void generateTemplate_unsafeTemplateName_throwsBadRequest() {
        EmailRequest request = EmailRequest.builder()
                .to("recipient@example.com")
                .subject("Test")
                .template("../../etc/passwd")
                .build();

        assertThatThrownBy(() -> emailService.generateTemplate(request))
                .isInstanceOf(EmailSendException.class)
                .satisfies(ex -> {
                    assertThat(((EmailSendException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getMessage()).contains("Invalid template name");
                });
    }

    @Test
    void generateTemplate_validName_callsEngine() {
        when(templateEngine.process(eq("welcome"), any())).thenReturn("<html>Hello</html>");

        EmailRequest request = EmailRequest.builder()
                .to("recipient@example.com")
                .subject("Test")
                .template("welcome")
                .build();

        String html = emailService.generateTemplate(request);
        assertThat(html).isEqualTo("<html>Hello</html>");
    }
}
