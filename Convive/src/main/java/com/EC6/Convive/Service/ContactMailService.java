package com.EC6.Convive.Service;

import com.EC6.Convive.Model.ContactMessageModel;
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

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.contact.to}")
    private String toAddress;

    public void send(ContactMessageModel dto) throws MailException {
        if (!StringUtils.hasText(fromAddress) || !StringUtils.hasText(toAddress)) {
            log.warn("Contato: envio abortado — SMTP incompleto (defina spring.mail.username e app.contact.to).");
            throw new IllegalStateException("SMTP não configurado: defina spring.mail.username e app.contact.to.");
        }

        int bodyLen = dto.getMessage() != null ? dto.getMessage().length() : 0;
        log.info("Contato: iniciando envio | assunto={} | tamanhoCorpo={}", dto.getSubject(), bodyLen);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(toAddress);
            helper.setReplyTo(dto.getEmail());
            helper.setSubject("[Convive Contato] " + dto.getSubject());
            helper.setText(buildBody(dto), false);
        } catch (jakarta.mail.MessagingException e) {
            log.error("Contato: falha ao montar MIME | causa={}", e.getMessage(), e);
            throw new MailPreparationException("Falha ao montar a mensagem.", e);
        }

        try {
            mailSender.send(message);
            log.info("Contato: e-mail enviado com sucesso | destino={} | assuntoMail=[Convive Contato] {}",
                    toAddress, dto.getSubject());
        } catch (MailException e) {
            log.error("Contato: falha no SMTP ao enviar | tipo={} | mensagem={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    public void sendToOutside(ContactMessageModel dto) throws MailException {
        if (!StringUtils.hasText(fromAddress) || !StringUtils.hasText(toAddress)) {
            log.warn("Contato: envio abortado — SMTP incompleto (defina spring.mail.username e app.contact.to).");
            throw new IllegalStateException("SMTP não configurado: defina spring.mail.username e app.contact.to.");
        }

        int bodyLen = dto.getMessage() != null ? dto.getMessage().length() : 0;
        log.info("Contato: iniciando envio | assunto={} | tamanhoCorpo={}", dto.getSubject(), bodyLen);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(dto.getEmail());
            helper.setReplyTo(dto.getEmail());
            helper.setSubject("[Convive Contato] " + dto.getSubject());
            helper.setText(buildBody(dto), false);
        } catch (jakarta.mail.MessagingException e) {
            log.error("Contato: falha ao montar MIME | causa={}", e.getMessage(), e);
            throw new MailPreparationException("Falha ao montar a mensagem.", e);
        }

        try {
            mailSender.send(message);
            log.info("Contato: e-mail enviado com sucesso | destino={} | assuntoMail=[Convive Contato] {}",
                    toAddress, dto.getSubject());
        } catch (MailException e) {
            log.error("Contato: falha no SMTP ao enviar | tipo={} | mensagem={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    private static String buildBody(ContactMessageModel dto) {
        return """
                Nova mensagem pelo site Convive.

                Nome: %s
                E-mail informado: %s

                Mensagem:
                %s
                """.formatted(dto.getFullName(), dto.getEmail(), dto.getMessage());
    }
}
