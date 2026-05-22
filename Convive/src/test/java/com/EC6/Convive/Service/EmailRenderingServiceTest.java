package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.email.RenderedEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailRenderingServiceTest {

    private EmailRenderingService emailRenderingService;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        emailRenderingService = new EmailRenderingService(templateEngine);
        ReflectionTestUtils.setField(emailRenderingService, "brandName", "Convive");
        ReflectionTestUtils.setField(emailRenderingService, "baseUrl", "http://localhost:8085");
        ReflectionTestUtils.setField(emailRenderingService, "expirationMinutes", 60);
    }

    @Test
    void renderPasswordReset_containsLinkAndExpiration() {
        RenderedEmail rendered = emailRenderingService.renderPasswordReset(
                "http://localhost:8085/reset-password?token=abc");

        assertAll(
                () -> assertNotNull(rendered.htmlBody()),
                () -> assertTrue(rendered.htmlBody().contains("Redefinir senha")),
                () -> assertTrue(rendered.htmlBody().contains("reset-password?token=abc")),
                () -> assertTrue(rendered.plainTextBody().contains("60 minutos")),
                () -> assertTrue(rendered.plainTextBody().contains("Equipe Convive"))
        );
    }

    @Test
    void renderContactInbound_containsSenderDetails() {
        ContactMessageModel dto = new ContactMessageModel();
        dto.setFullName("João Silva");
        dto.setEmail("joao@empresa.com.br");
        dto.setSubject("Demonstração");
        dto.setMessage("Gostaria de agendar uma demonstração.");

        RenderedEmail rendered = emailRenderingService.renderContactInbound(dto);

        assertAll(
                () -> assertTrue(rendered.htmlBody().contains("João Silva")),
                () -> assertTrue(rendered.htmlBody().contains("joao@empresa.com.br")),
                () -> assertTrue(rendered.plainTextBody().contains("Demonstração"))
        );
    }

    @Test
    void renderOcorrenciaNotification_containsFormattedDateAndModerator() {
        OcorrenciaCriadaEvent event = new OcorrenciaCriadaEvent(
                LocalDateTime.of(2026, 5, 19, 14, 30),
                "Maria",
                "Barulho excessivo no andar de cima",
                "Barulho",
                "Ruído após 23h no apartamento 302."
        );

        RenderedEmail rendered = emailRenderingService.renderOcorrenciaNotification("Ana", event);

        assertAll(
                () -> assertTrue(rendered.htmlBody().contains("Ana")),
                () -> assertTrue(rendered.htmlBody().contains("19/05/2026 às 14:30")),
                () -> assertTrue(rendered.htmlBody().contains("triagemOcorrencias")),
                () -> assertTrue(rendered.htmlBody().contains("Barulho excessivo no andar de cima")),
                () -> assertTrue(rendered.plainTextBody().contains("Maria")),
                () -> assertTrue(rendered.plainTextBody().contains("Barulho"))
        );
    }

    @Test
    void renderReservaPendenteNotification_containsReservationDetails() {
        ReservaPendenteCriadaEvent event = new ReservaPendenteCriadaEvent(
                "Salão de festas",
                "Carlos",
                LocalDateTime.of(2026, 6, 10, 18, 0),
                "Aniversário"
        );

        RenderedEmail rendered = emailRenderingService.renderReservaPendenteNotification("Bruno", event);

        assertAll(
                () -> assertTrue(rendered.htmlBody().contains("Salão de festas")),
                () -> assertTrue(rendered.htmlBody().contains("triagemReservas")),
                () -> assertTrue(rendered.plainTextBody().contains("10/06/2026 às 18:00"))
        );
    }

    @Test
    void renderReservaRejeitadaNotification_containsRejectionReason() {
        ReservaRejeitadaEvent event = new ReservaRejeitadaEvent(
                "morador@email.com",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                "Horário indisponível"
        );

        RenderedEmail rendered = emailRenderingService.renderReservaRejeitadaNotification(event);

        assertAll(
                () -> assertTrue(rendered.htmlBody().contains("Horário indisponível")),
                () -> assertTrue(rendered.htmlBody().contains("/morador/reservas")),
                () -> assertTrue(rendered.plainTextBody().contains("não foi aprovada"))
        );
    }
}
