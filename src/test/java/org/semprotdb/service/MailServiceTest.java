package org.semprotdb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.domain.User;
import org.semprotdb.repository.DBConfigRepository;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tech.jhipster.config.JHipsterProperties;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private DBConfigRepository dbConfigRepository;

    private final JHipsterProperties properties = new JHipsterProperties();
    private MailService mailService;

    @BeforeEach
    void setup() {
        properties.getMail().setFrom("noreply@example.com");
        properties.getMail().setBaseUrl("https://semprotdb.org");

        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("i18n/messages");
        messages.setDefaultEncoding("UTF-8");

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setTemplateEngineMessageSource(messages);

        mailService = new MailService(properties, javaMailSender, messages, engine, dbConfigRepository);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void sendsMessageThroughConfiguredSender(boolean html) throws Exception {
        mailService.sendEmail("recipient@example.com", "Subject", "Message", false, html);

        MimeMessage message = sentMessage();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("recipient@example.com");
        assertThat(message.getFrom()[0].toString()).isEqualTo("noreply@example.com");
        assertThat(message.getSubject()).isEqualTo("Subject");
        assertThat(message.getContent()).isEqualTo("Message");
        assertThat(message.getDataHandler().getContentType()).startsWith(html ? "text/html" : "text/plain");
    }

    @ParameterizedTest
    @ValueSource(strings = { "https://semprotdb.org", "https://configured.example/application" })
    void resetEmailAndHistoryUseConfiguredBaseUrl(String baseUrl) throws Exception {
        properties.getMail().setBaseUrl(baseUrl);
        User user = user();
        user.setResetKey("test-reset-key");

        mailService.sendPasswordResetMail(user);

        String resetUrl = baseUrl + "/account/reset/finish?key=test-reset-key";
        String content = sentMessage().getContent().toString();
        assertThat(content).contains("href=\"" + resetUrl + "\"").doesNotContain("bioinformatics.icb.ufmg.br");

        ArgumentCaptor<DBConfig> records = ArgumentCaptor.forClass(DBConfig.class);
        verify(dbConfigRepository, org.mockito.Mockito.times(2)).saveAndFlush(records.capture());
        List<DBConfig> history = records.getAllValues();
        assertThat(history)
            .filteredOn(record -> "RESET SENHA".equals(record.getKey()))
            .singleElement()
            .satisfies(record -> {
                assertThat(record.getVstring()).isEqualTo(resetUrl);
                assertThat(record.getVdate()).isNotNull();
            });
        assertThat(history)
            .filteredOn(record -> "EMAIL RESET SENHA".equals(record.getKey()))
            .singleElement()
            .satisfies(record -> assertThat(record.getVtext()).isEqualTo(content));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void accountEmailsUseProductionDomain(boolean activation) throws Exception {
        User user = user();
        user.setActivationKey("test-activation-key");
        user.setResetKey("test-reset-key");

        if (activation) {
            mailService.sendActivationEmail(user);
        } else {
            mailService.sendCreationEmail(user);
        }

        String path = activation ? "/account/activate?key=test-activation-key" : "/account/reset/finish?key=test-reset-key";
        assertThat(sentMessage().getContent().toString()).contains("href=\"https://semprotdb.org" + path + "\"");
    }

    @Test
    void smtpFailureDoesNotEscapeMailService() {
        doThrow(new MailSendException("SMTP unavailable")).when(javaMailSender).send(any(MimeMessage.class));

        assertThatCode(() -> mailService.sendEmail("recipient@example.com", "Subject", "Message", false, true)).doesNotThrowAnyException();
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    private MimeMessage sentMessage() {
        ArgumentCaptor<MimeMessage> message = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(message.capture());
        return message.getValue();
    }

    private User user() {
        User user = new User();
        user.setLogin("test-user");
        user.setEmail("recipient@example.com");
        user.setLangKey("pt-br");
        return user;
    }
}
