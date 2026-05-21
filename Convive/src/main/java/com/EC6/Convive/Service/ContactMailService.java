package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.email.RenderedEmail;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactMailService {

    private static final String SUBJECT_CONTACT = "Convive — Mensagem de contato: %s";
    private static final String SUBJECT_OCORRENCIA = "Convive — Nova ocorrência aguardando triagem";
    private static final String SUBJECT_RESERVA_PENDENTE = "Convive — Nova reserva pendente de aprovação";
    private static final String SUBJECT_RESERVA_REJEITADA = "Convive — Atualização da sua reserva de área comum";

    private final JavaMailSender mailSender;
    private final EmailRenderingService emailRenderingService;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.contact.to}")
    private String toAddress;

    public void send(ContactMessageModel dto) throws MailException {
        ensureInboundConfigured();
        RenderedEmail rendered = emailRenderingService.renderContactInbound(dto);
        String subject = SUBJECT_CONTACT.formatted(dto.getSubject());
        log.info("Contato: iniciando envio | assunto={}", dto.getSubject());
        deliver(fromAddress, toAddress, subject, rendered, dto.getEmail());
        log.info("Contato: e-mail enviado com sucesso | destino={} | assuntoMail={}", toAddress, subject);
    }

    public void sendOcorrenciaNotification(String moderatorEmail, String moderatorName, OcorrenciaCriadaEvent event)
            throws MailException {
        ensureOutboundConfigured();
        RenderedEmail rendered = emailRenderingService.renderOcorrenciaNotification(moderatorName, event);
        log.info("Notificação ocorrência: iniciando envio | destino={}", moderatorEmail);
        deliver(fromAddress, moderatorEmail, SUBJECT_OCORRENCIA, rendered, null);
        log.info("Notificação ocorrência: e-mail enviado | destino={}", moderatorEmail);
    }

    public void sendReservaPendenteNotification(String moderatorEmail, String moderatorName,
                                                ReservaPendenteCriadaEvent event) throws MailException {
        ensureOutboundConfigured();
        RenderedEmail rendered = emailRenderingService.renderReservaPendenteNotification(moderatorName, event);
        log.info("Notificação reserva pendente: iniciando envio | destino={}", moderatorEmail);
        deliver(fromAddress, moderatorEmail, SUBJECT_RESERVA_PENDENTE, rendered, null);
        log.info("Notificação reserva pendente: e-mail enviado | destino={}", moderatorEmail);
    }

    public void sendReservaRejeitadaNotification(ReservaRejeitadaEvent event) throws MailException {
        ensureOutboundConfigured();
        RenderedEmail rendered = emailRenderingService.renderReservaRejeitadaNotification(event);
        String destino = event.moradorEmail();
        log.info("Notificação reserva rejeitada: iniciando envio | destino={}", destino);
        deliver(fromAddress, destino, SUBJECT_RESERVA_REJEITADA, rendered, null);
        log.info("Notificação reserva rejeitada: e-mail enviado | destino={}", destino);
    }

    private void deliver(String from, String to, String subject, RenderedEmail rendered, String replyTo)
            throws MailException {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(rendered.plainTextBody(), rendered.htmlBody());
            if (StringUtils.hasText(replyTo)) {
                helper.setReplyTo(replyTo);
            }
        } catch (jakarta.mail.MessagingException e) {
            log.error("E-mail: falha ao montar MIME | destino={} | causa={}", to, e.getMessage(), e);
            throw new MailPreparationException("Falha ao montar a mensagem.", e);
        }

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("E-mail: falha no SMTP | destino={} | tipo={} | mensagem={}",
                    to, e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    private void ensureInboundConfigured() {
        if (!StringUtils.hasText(fromAddress) || !StringUtils.hasText(toAddress)) {
            log.warn("Contato: envio abortado — SMTP incompleto (defina spring.mail.username e app.contact.to).");
            throw new IllegalStateException("SMTP não configurado: defina spring.mail.username e app.contact.to.");
        }
    }

    private void ensureOutboundConfigured() {
        if (!StringUtils.hasText(fromAddress)) {
            log.warn("Notificação: envio abortado — SMTP incompleto (defina spring.mail.username).");
            throw new IllegalStateException("SMTP não configurado: defina spring.mail.username.");
        }
    }
}
