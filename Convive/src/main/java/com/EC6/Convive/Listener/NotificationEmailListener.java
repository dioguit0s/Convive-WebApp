package com.EC6.Convive.Listener;

import com.EC6.Convive.Config.AsyncConfig;
import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.Service.ModeradorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEmailListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM ");

    private final ContactMailService contactMailService;
    private final ModeradorService moderadorService;

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onOcorrenciaCriada(OcorrenciaCriadaEvent event) {
        ContactMessageModel base = new ContactMessageModel();
        base.setSubject("Nova ocorrencia registrada e necessitando triagem");
        base.setMessage("""
                Um usuario cadastrou uma nova ocorrencia as %s
                Detalhes:
                Registrado por: %s
                Descrição: %s
                """.formatted(
                event.dataRegistro().format(FORMATTER),
                event.nomeMorador(),
                event.descricao()
        ));
        notifyModeradores(base, "OcorrenciaCriada");
    }

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onReservaPendenteCriada(ReservaPendenteCriadaEvent event) {
        ContactMessageModel base = new ContactMessageModel();
        base.setSubject("Nova reserva pendente de aprovação");
        base.setMessage("""
                Verifique a pagina de triagem de Reservas para aprovar ou rejeitar a nova reserva
                Detalhes:
                Area da Reserva: %s
                Morador: %s
                Data da Reserva: %s
                Observação: %s
                """.formatted(
                event.areaNome(),
                event.moradorNome(),
                event.inicio().format(FORMATTER),
                event.observacoes() != null ? event.observacoes() : ""
        ));
        notifyModeradores(base, "ReservaPendenteCriada");
    }

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onReservaRejeitada(ReservaRejeitadaEvent event) {
        ContactMessageModel mensagem = new ContactMessageModel();
        mensagem.setEmail(event.moradorEmail());
        mensagem.setSubject("Reserva rejeitada");
        mensagem.setMessage("""
                Sua reserva de area comum foi rejeitada para o dia: %s

                Mensagem do moderador:
                %s.

                Verifique o seu portal para mais informacoes
                """.formatted(
                event.inicio().format(FORMATTER),
                event.motivoRejeicao()
        ));
        sendSafely(mensagem, event.moradorEmail(), "ReservaRejeitada");
    }

    private void notifyModeradores(ContactMessageModel base, String eventName) {
        List<Moderador> moderadores = moderadorService.getAllActiveMods();
        for (Moderador mod : moderadores) {
            ContactMessageModel message = new ContactMessageModel();
            message.setSubject(base.getSubject());
            message.setMessage(base.getMessage());
            message.setEmail(mod.getEmail());
            message.setFullName(mod.getNome());
            sendSafely(message, mod.getEmail(), eventName);
        }
    }

    private void sendSafely(ContactMessageModel message, String destino, String eventName) {
        try {
            contactMailService.sendToOutside(message);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação | evento={} | destino={} | causa={}",
                    eventName, destino, e.getMessage(), e);
        }
    }
}
