package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OcorrenciaServiceTest {

    @Mock private OcorrenciaRepository ocorrenciaRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private OcorrenciaService ocorrenciaService;

    @Test
    void ocorrenciaService_SearchById_Sucesso() {
        UUID id = UUID.randomUUID();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setDescricao("Lâmpada fundida no corredor");

        when(ocorrenciaRepository.findById(id)).thenReturn(Optional.of(ocorrencia));

        Ocorrencia encontrada = ocorrenciaService.searchById(id);

        assertNotNull(encontrada);
        assertEquals("Lâmpada fundida no corredor", encontrada.getDescricao());
    }

    @Test
    void ocorrenciaService_SearchById_ErroNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(ocorrenciaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ocorrenciaService.searchById(id));
    }

    @Test
    void ocorrenciaService_Insert_SalvaAntesDePublicarEvento() {
        Morador morador = new Morador();
        morador.setNome("João Silva");

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setDescricao("Vazamento no hall");
        ocorrencia.setUsuario(morador);

        when(ocorrenciaRepository.findTopByProtocoloStartingWithOrderByProtocoloDesc(any()))
                .thenReturn(Optional.empty());
        when(ocorrenciaRepository.save(any(Ocorrencia.class))).thenAnswer(invocation -> {
            Ocorrencia arg = invocation.getArgument(0);
            arg.setDataRegistro(LocalDateTime.of(2026, 5, 19, 10, 0));
            return arg;
        });

        Ocorrencia salva = ocorrenciaService.insert(ocorrencia);

        assertNotNull(salva.getProtocolo());
        verify(ocorrenciaRepository).save(ocorrencia);

        ArgumentCaptor<OcorrenciaCriadaEvent> eventCaptor = ArgumentCaptor.forClass(OcorrenciaCriadaEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        OcorrenciaCriadaEvent event = eventCaptor.getValue();
        assertEquals("João Silva", event.nomeMorador());
        assertEquals("Vazamento no hall", event.descricao());
        assertEquals(LocalDateTime.of(2026, 5, 19, 10, 0), event.dataRegistro());
    }
}
