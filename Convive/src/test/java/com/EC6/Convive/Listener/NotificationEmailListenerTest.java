package com.EC6.Convive.Listener;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.ContactMessageModel;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEmailListenerTest {

    @Mock private ContactMailService contactMailService;
    @Mock private ModeradorService moderadorService;
    @InjectMocks private NotificationEmailListener listener;

    @Test
    void onOcorrenciaCriada_notificaTodosModeradores() {
        Moderador mod1 = new Moderador();
        mod1.setNome("Ana");
        mod1.setEmail("ana@convive.com");
        Moderador mod2 = new Moderador();
        mod2.setNome("Bruno");
        mod2.setEmail("bruno@convive.com");

        when(moderadorService.getAllActiveMods()).thenReturn(List.of(mod1, mod2));

        OcorrenciaCriadaEvent event = new OcorrenciaCriadaEvent(
                LocalDateTime.of(2026, 5, 19, 14, 30),
                "Maria",
                "Barulho excessivo"
        );

        listener.onOcorrenciaCriada(event);

        verify(contactMailService, times(2)).sendToOutside(any(ContactMessageModel.class));
    }

    @Test
    void onOcorrenciaCriada_falhaEmailNaoPropaga() {
        Moderador mod = new Moderador();
        mod.setNome("Ana");
        mod.setEmail("ana@convive.com");

        when(moderadorService.getAllActiveMods()).thenReturn(List.of(mod));
        doThrow(new MailSendException("SMTP indisponível"))
                .when(contactMailService).sendToOutside(any(ContactMessageModel.class));

        OcorrenciaCriadaEvent event = new OcorrenciaCriadaEvent(
                LocalDateTime.of(2026, 5, 19, 14, 30),
                "Maria",
                "Barulho excessivo"
        );

        assertDoesNotThrow(() -> listener.onOcorrenciaCriada(event));
    }

    @Test
    void onReservaRejeitada_enviaEmailAoMorador() {
        ReservaRejeitadaEvent event = new ReservaRejeitadaEvent(
                "morador@email.com",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                "Horário indisponível"
        );

        listener.onReservaRejeitada(event);

        verify(contactMailService).sendToOutside(argThat(msg ->
                "morador@email.com".equals(msg.getEmail())
                        && msg.getSubject().contains("rejeitada")
        ));
    }

    @Test
    void onReservaRejeitada_falhaEmailNaoPropaga() {
        doThrow(new MailSendException("SMTP indisponível"))
                .when(contactMailService).sendToOutside(any(ContactMessageModel.class));

        ReservaRejeitadaEvent event = new ReservaRejeitadaEvent(
                "morador@email.com",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                "Horário indisponível"
        );

        assertDoesNotThrow(() -> listener.onReservaRejeitada(event));
    }
}
