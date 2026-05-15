package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Repository.ComunicadoRepository;
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
public class ComunicadoServiceTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;
    @InjectMocks
    private ComunicadoService comunicadoService;

    @Test
    void comunicadoService_SearchById_Sucesso() {
        UUID id = UUID.randomUUID();
        Comunicado comunicado = new Comunicado();
        comunicado.setId(id);
        comunicado.setTitulo("Aviso de Manutenção");

        when(comunicadoRepository.findById(id)).thenReturn(Optional.of(comunicado));

        Comunicado encontrado = comunicadoService.searchById(id);

        assertNotNull(encontrado);
        assertEquals("Aviso de Manutenção", encontrado.getTitulo());
    }

    @Test
    void comunicadoService_SearchById_ErroNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(comunicadoRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> comunicadoService.searchById(id));
        assertEquals("Comunicado não encontrada com o ID: " + id, exception.getMessage());
    }
}
