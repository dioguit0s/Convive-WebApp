package com.EC6.Convive.Service;

import com.EC6.Convive.Config.AsyncConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.base-url}")
    private String baseUrl;

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    public void sendResetLink(String toEmail, String token) {
        if (!StringUtils.hasText(fromAddress)) {
            log.warn("Reset de senha: envio abortado — SMTP incompleto (defina spring.mail.username).");
            return;
        }

        String resetUrl = baseUrl.replaceAll("/$", "") + "/reset-password?token=" + token;
        String subject = "[Convive] Redefinição de senha";
        String body = """
                Olá,

                Recebemos uma solicitação para redefinir a senha da sua conta Convive.

                Para criar uma nova senha, acesse o link abaixo (válido por tempo limitado):

                %s

                Se você não solicitou esta alteração, ignore este e-mail. Sua senha permanecerá inalterada.

                Atenciosamente,
                Equipe Convive
                """.formatted(resetUrl);

        log.info("Reset de senha: iniciando envio | destino={}", toEmail);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
        } catch (jakarta.mail.MessagingException e) {
            log.error("Reset de senha: falha ao montar MIME | causa={}", e.getMessage(), e);
            throw new MailPreparationException("Falha ao montar a mensagem de redefinição.", e);
        }

        try {
            mailSender.send(message);
            log.info("Reset de senha: e-mail enviado com sucesso | destino={}", toEmail);
        } catch (MailException e) {
            log.error("Reset de senha: falha no SMTP | destino={} | causa={}", toEmail, e.getMessage(), e);
            throw e;
        }
    }
}
