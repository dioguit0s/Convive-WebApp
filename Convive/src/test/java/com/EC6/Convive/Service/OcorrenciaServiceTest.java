package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OcorrenciaServiceTest {

    @Mock private OcorrenciaRepository ocorrenciaRepository;
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
}
