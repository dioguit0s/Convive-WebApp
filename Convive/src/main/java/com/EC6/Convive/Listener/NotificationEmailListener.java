package com.EC6.Convive.Listener;

import com.EC6.Convive.Config.AsyncConfig;
import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.Service.ModeradorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEmailListener {

    private final ContactMailService contactMailService;
    private final ModeradorService moderadorService;

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onOcorrenciaCriada(OcorrenciaCriadaEvent event) {
        notifyModeradoresOcorrencia(event);
    }

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onReservaPendenteCriada(ReservaPendenteCriadaEvent event) {
        notifyModeradoresReservaPendente(event);
    }

    @Async(AsyncConfig.MAIL_TASK_EXECUTOR)
    @EventListener
    public void onReservaRejeitada(ReservaRejeitadaEvent event) {
        sendSafelyReservaRejeitada(event);
    }

    private void notifyModeradoresOcorrencia(OcorrenciaCriadaEvent event) {
        List<Moderador> moderadores = moderadorService.getAllActiveMods();
        for (Moderador mod : moderadores) {
            sendSafely(() -> contactMailService.sendOcorrenciaNotification(
                    mod.getEmail(), mod.getNome(), event), mod.getEmail(), "OcorrenciaCriada");
        }
    }

    private void notifyModeradoresReservaPendente(ReservaPendenteCriadaEvent event) {
        List<Moderador> moderadores = moderadorService.getAllActiveMods();
        for (Moderador mod : moderadores) {
            sendSafely(() -> contactMailService.sendReservaPendenteNotification(
                    mod.getEmail(), mod.getNome(), event), mod.getEmail(), "ReservaPendenteCriada");
        }
    }

    private void sendSafelyReservaRejeitada(ReservaRejeitadaEvent event) {
        sendSafely(() -> contactMailService.sendReservaRejeitadaNotification(event),
                event.moradorEmail(), "ReservaRejeitada");
    }

    private void sendSafely(Runnable sendAction, String destino, String eventName) {
        try {
            sendAction.run();
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação | evento={} | destino={} | causa={}",
                    eventName, destino, e.getMessage(), e);
        }
    }
}
