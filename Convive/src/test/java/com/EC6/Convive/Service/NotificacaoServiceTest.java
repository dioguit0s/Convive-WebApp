package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Repository.NotificacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacaoServiceTest {

    @Mock private NotificacaoRepository notificacaoRepository;
    @InjectMocks private NotificacaoService notificacaoService;

    @Test
    void notificacaoService_Insert_Sucesso() {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nova reserva criada");

        when(notificacaoRepository.save(notificacao)).thenReturn(notificacao);

        Notificacao salva = notificacaoService.insert(notificacao);

        assertNotNull(salva);
        assertEquals("Nova reserva criada", salva.getDescricao());
        verify(notificacaoRepository, times(1)).save(notificacao);
    }

    @Test
    void notificacaoService_SearchById_ErroNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(notificacaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificacaoService.searchById(id));
    }
}