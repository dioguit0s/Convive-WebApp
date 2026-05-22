package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.email.EmailDetailRow;
import com.EC6.Convive.Model.email.RenderedEmail;
import com.EC6.Convive.Util.EmailDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailRenderingService {

    private static final String TEMPLATE_PASSWORD_RESET = "email/password-reset";
    private static final String TEMPLATE_CONTACT_INBOUND = "email/contact-inbound";
    private static final String TEMPLATE_OCORRENCIA = "email/notification-ocorrencia";
    private static final String TEMPLATE_RESERVA_PENDENTE = "email/notification-reserva-pendente";
    private static final String TEMPLATE_RESERVA_REJEITADA = "email/notification-reserva-rejeitada";

    private final TemplateEngine templateEngine;

    @Value("${app.mail.brand-name:Convive}")
    private String brandName;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.password-reset.expiration-minutes:60}")
    private int expirationMinutes;

    public RenderedEmail renderPasswordReset(String resetUrl) {
        Context context = baseContext();
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("expirationMinutes", expirationMinutes);

        String html = process(TEMPLATE_PASSWORD_RESET, context);
        String plain = """
                Prezado(a) usuário(a),

                Recebemos uma solicitação para redefinir a senha da sua conta no sistema Convive.

                Para prosseguir, acesse o link abaixo (válido por %d minutos):
                %s

                Se você não solicitou esta alteração, ignore este e-mail. Sua senha permanecerá inalterada.

                Atenciosamente,
                Equipe Convive
                """.formatted(expirationMinutes, resetUrl);

        return new RenderedEmail(html, plain);
    }

    public RenderedEmail renderContactInbound(ContactMessageModel dto) {
        Context context = baseContext();
        context.setVariable("detailRows", List.of(
                new EmailDetailRow("Nome", dto.getFullName()),
                new EmailDetailRow("E-mail", dto.getEmail()),
                new EmailDetailRow("Assunto", dto.getSubject())
        ));
        context.setVariable("messageBody", dto.getMessage());

        String html = process(TEMPLATE_CONTACT_INBOUND, context);
        String plain = """
                Prezada equipe,

                Nova mensagem pelo formulário de contato do site Convive.

                Nome: %s
                E-mail: %s
                Assunto: %s

                Mensagem:
                %s
                """.formatted(dto.getFullName(), dto.getEmail(), dto.getSubject(), dto.getMessage());

        return new RenderedEmail(html, plain);
    }

    public RenderedEmail renderOcorrenciaNotification(String recipientName, OcorrenciaCriadaEvent event) {
        String actionUrl = normalizedBaseUrl() + "/moderador/triagemOcorrencias";
        Context context = baseContext();
        context.setVariable("recipientName", recipientName);
        context.setVariable("actionUrl", actionUrl);
        context.setVariable("detailRows", List.of(
                new EmailDetailRow("Data do registro", EmailDateFormatter.format(event.dataRegistro())),
                new EmailDetailRow("Registrado por", event.nomeMorador()),
                new EmailDetailRow("Título", event.titulo()),
                new EmailDetailRow("Categoria", event.categoria()),
                new EmailDetailRow("Descrição", event.descricao())
        ));

        String html = process(TEMPLATE_OCORRENCIA, context);
        String plain = """
                Prezado(a) %s,

                Uma nova ocorrência foi registrada e aguarda triagem.

                Data do registro: %s
                Registrado por: %s
                Título: %s
                Categoria: %s
                Descrição: %s

                Acesse: %s

                Atenciosamente,
                Equipe Convive
                """.formatted(
                recipientName,
                EmailDateFormatter.format(event.dataRegistro()),
                event.nomeMorador(),
                event.titulo(),
                event.categoria(),
                event.descricao(),
                actionUrl
        );

        return new RenderedEmail(html, plain);
    }

    public RenderedEmail renderReservaPendenteNotification(String recipientName, ReservaPendenteCriadaEvent event) {
        String actionUrl = normalizedBaseUrl() + "/moderador/triagemReservas";
        String observacoes = StringUtils.hasText(event.observacoes()) ? event.observacoes() : "—";

        Context context = baseContext();
        context.setVariable("recipientName", recipientName);
        context.setVariable("actionUrl", actionUrl);
        context.setVariable("detailRows", List.of(
                new EmailDetailRow("Área", event.areaNome()),
                new EmailDetailRow("Morador", event.moradorNome()),
                new EmailDetailRow("Data da reserva", EmailDateFormatter.format(event.inicio())),
                new EmailDetailRow("Observação", observacoes)
        ));

        String html = process(TEMPLATE_RESERVA_PENDENTE, context);
        String plain = """
                Prezado(a) %s,

                Nova reserva de área comum aguarda aprovação.

                Área: %s
                Morador: %s
                Data da reserva: %s
                Observação: %s

                Acesse: %s

                Atenciosamente,
                Equipe Convive
                """.formatted(
                recipientName,
                event.areaNome(),
                event.moradorNome(),
                EmailDateFormatter.format(event.inicio()),
                observacoes,
                actionUrl
        );

        return new RenderedEmail(html, plain);
    }

    public RenderedEmail renderReservaRejeitadaNotification(ReservaRejeitadaEvent event) {
        String actionUrl = normalizedBaseUrl() + "/morador/reservas";
        Context context = baseContext();
        context.setVariable("actionUrl", actionUrl);
        context.setVariable("motivoRejeicao", event.motivoRejeicao());
        context.setVariable("detailRows", List.of(
                new EmailDetailRow("Data da reserva", EmailDateFormatter.format(event.inicio()))
        ));

        String html = process(TEMPLATE_RESERVA_REJEITADA, context);
        String plain = """
                Prezado(a) condômino(a),

                A sua solicitação de reserva de área comum não foi aprovada.

                Data da reserva: %s

                Parecer da moderação:
                %s

                Consulte mais informações em: %s

                Atenciosamente,
                Equipe Convive
                """.formatted(
                EmailDateFormatter.format(event.inicio()),
                event.motivoRejeicao(),
                actionUrl
        );

        return new RenderedEmail(html, plain);
    }

    private Context baseContext() {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("portalUrl", normalizedBaseUrl());
        return context;
    }

    private String normalizedBaseUrl() {
        return baseUrl.replaceAll("/$", "");
    }

    private String process(String template, Context context) {
        return templateEngine.process(template, context);
    }
}
