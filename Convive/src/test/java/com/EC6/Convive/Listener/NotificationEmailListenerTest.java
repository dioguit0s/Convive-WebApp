package com.EC6.Convive.Listener;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.Service.ModeradorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEmailListenerTest {

    @Mock private ContactMailService contactMailService;
    @Mock private ModeradorService moderadorService;
    @InjectMocks private NotificationEmailListener listener;

    @Test
    void onOcorrenciaCriada_notificaTodosModeradores() {
        Moderador mod1 = moderador("Ana", "ana@convive.com");
        Moderador mod2 = moderador("Bruno", "bruno@convive.com");

        when(moderadorService.getAllActiveMods()).thenReturn(List.of(mod1, mod2));

        OcorrenciaCriadaEvent event = new OcorrenciaCriadaEvent(
                LocalDateTime.of(2026, 5, 19, 14, 30),
                "Maria",
                "Barulho excessivo"
        );

        listener.onOcorrenciaCriada(event);

        verify(contactMailService).sendOcorrenciaNotification("ana@convive.com", "Ana", event);
        verify(contactMailService).sendOcorrenciaNotification("bruno@convive.com", "Bruno", event);
    }

    @Test
    void onOcorrenciaCriada_falhaEmailNaoPropaga() {
        Moderador mod = moderador("Ana", "ana@convive.com");

        when(moderadorService.getAllActiveMods()).thenReturn(List.of(mod));
        doThrow(new MailSendException("SMTP indisponível"))
                .when(contactMailService).sendOcorrenciaNotification(any(), any(), any());

        OcorrenciaCriadaEvent event = new OcorrenciaCriadaEvent(
                LocalDateTime.of(2026, 5, 19, 14, 30),
                "Maria",
                "Barulho excessivo"
        );

        assertDoesNotThrow(() -> listener.onOcorrenciaCriada(event));
    }

    @Test
    void onReservaPendenteCriada_notificaTodosModeradores() {
        Moderador mod = moderador("Ana", "ana@convive.com");
        when(moderadorService.getAllActiveMods()).thenReturn(List.of(mod));

        ReservaPendenteCriadaEvent event = new ReservaPendenteCriadaEvent(
                "Piscina",
                "João",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                null
        );

        listener.onReservaPendenteCriada(event);

        verify(contactMailService).sendReservaPendenteNotification("ana@convive.com", "Ana", event);
    }

    @Test
    void onReservaRejeitada_enviaEmailAoMorador() {
        ReservaRejeitadaEvent event = new ReservaRejeitadaEvent(
                "morador@email.com",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                "Horário indisponível"
        );

        listener.onReservaRejeitada(event);

        verify(contactMailService).sendReservaRejeitadaNotification(eq(event));
    }

    @Test
    void onReservaRejeitada_falhaEmailNaoPropaga() {
        doThrow(new MailSendException("SMTP indisponível"))
                .when(contactMailService).sendReservaRejeitadaNotification(any());

        ReservaRejeitadaEvent event = new ReservaRejeitadaEvent(
                "morador@email.com",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                "Horário indisponível"
        );

        assertDoesNotThrow(() -> listener.onReservaRejeitada(event));
    }

    private static Moderador moderador(String nome, String email) {
        Moderador mod = new Moderador();
        mod.setNome(nome);
        mod.setEmail(email);
        return mod;
    }
}
