package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Model.CategoriaOcorrencia;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        ocorrencia.setTitulo("Lâmpada fundida");
        ocorrencia.setDescricao("Lâmpada fundida no corredor");

        when(ocorrenciaRepository.findById(id)).thenReturn(Optional.of(ocorrencia));

        Ocorrencia encontrada = ocorrenciaService.searchById(id);

        assertNotNull(encontrada);
        assertEquals("Lâmpada fundida", encontrada.getTitulo());
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
        ocorrencia.setTitulo("Vazamento no hall");
        ocorrencia.setCategoria(CategoriaOcorrencia.INFRAESTRUTURA);
        ocorrencia.setDescricao("Vazamento no hall do bloco A");
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
        assertEquals(Prioridade.ALTA, salva.getPrioridade());
        verify(ocorrenciaRepository).save(ocorrencia);

        ArgumentCaptor<OcorrenciaCriadaEvent> eventCaptor = ArgumentCaptor.forClass(OcorrenciaCriadaEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        OcorrenciaCriadaEvent event = eventCaptor.getValue();
        assertEquals("João Silva", event.nomeMorador());
        assertEquals("Vazamento no hall", event.titulo());
        assertEquals("Infraestrutura", event.categoria());
        assertEquals("Vazamento no hall do bloco A", event.descricao());
        assertEquals(LocalDateTime.of(2026, 5, 19, 10, 0), event.dataRegistro());
    }

    @Test
    void ocorrenciaService_Insert_Barulho_AplicaPrioridadeAlta() {
        Morador morador = new Morador();
        morador.setNome("Ana");

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTitulo("Barulho noturno");
        ocorrencia.setCategoria(CategoriaOcorrencia.BARULHO);
        ocorrencia.setDescricao("Barulho após 23h");
        ocorrencia.setUsuario(morador);

        when(ocorrenciaRepository.findTopByProtocoloStartingWithOrderByProtocoloDesc(any()))
                .thenReturn(Optional.empty());
        when(ocorrenciaRepository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ocorrencia salva = ocorrenciaService.insert(ocorrencia);

        assertEquals(Prioridade.ALTA, salva.getPrioridade());
    }

    @Test
    void ocorrenciaService_Insert_Outro_AplicaPrioridadeNaoDefinida() {
        Morador morador = new Morador();
        morador.setNome("Ana");

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTitulo("Problema diverso");
        ocorrencia.setCategoria(CategoriaOcorrencia.OUTRO);
        ocorrencia.setDescricao("Situação não listada");
        ocorrencia.setUsuario(morador);

        when(ocorrenciaRepository.findTopByProtocoloStartingWithOrderByProtocoloDesc(any()))
                .thenReturn(Optional.empty());
        when(ocorrenciaRepository.save(any(Ocorrencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ocorrencia salva = ocorrenciaService.insert(ocorrencia);

        assertEquals(Prioridade.NAO_DEFINIDA, salva.getPrioridade());
    }

    @Test
    void ocorrenciaService_Update_NaoReaplicaPrioridadePadrao() {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTitulo("Barulho");
        ocorrencia.setCategoria(CategoriaOcorrencia.BARULHO);
        ocorrencia.setPrioridade(Prioridade.BAIXA);

        when(ocorrenciaRepository.save(ocorrencia)).thenReturn(ocorrencia);

        Ocorrencia atualizada = ocorrenciaService.update(ocorrencia);

        assertEquals(Prioridade.BAIXA, atualizada.getPrioridade());
    }

    @Test
    void ocorrenciaService_DeleteForUser_MoradorAutor_Sucesso() {
        UUID ocorrenciaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Morador morador = new Morador();
        morador.setId(usuarioId);
        Ocorrencia ocorrencia = new Ocorrencia();

        when(ocorrenciaRepository.findByIdAndUsuario_Id(ocorrenciaId, usuarioId))
                .thenReturn(Optional.of(ocorrencia));

        boolean resultado = ocorrenciaService.deleteForUser(ocorrenciaId, morador);

        assertTrue(resultado);
        verify(ocorrenciaRepository, times(1)).delete(ocorrencia);
    }

    @Test
    void ocorrenciaService_DeleteForUser_MoradorNaoAutor_Falha() {
        UUID ocorrenciaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Morador morador = new Morador();
        morador.setId(usuarioId);

        when(ocorrenciaRepository.findByIdAndUsuario_Id(ocorrenciaId, usuarioId))
                .thenReturn(Optional.empty());

        boolean resultado = ocorrenciaService.deleteForUser(ocorrenciaId, morador);

        assertFalse(resultado);
        verify(ocorrenciaRepository, never()).delete(any());
    }

    @Test
    void ocorrenciaService_DeleteForUser_Moderador_Sucesso() {
        UUID ocorrenciaId = UUID.randomUUID();
        Moderador moderador = new Moderador();
        moderador.setId(UUID.randomUUID());
        Ocorrencia ocorrencia = new Ocorrencia();

        when(ocorrenciaRepository.findById(ocorrenciaId)).thenReturn(Optional.of(ocorrencia));

        boolean resultado = ocorrenciaService.deleteForUser(ocorrenciaId, moderador);

        assertTrue(resultado);
        verify(ocorrenciaRepository, times(1)).delete(ocorrencia);
        verify(ocorrenciaRepository, never()).findByIdAndUsuario_Id(any(), any());
    }
}
