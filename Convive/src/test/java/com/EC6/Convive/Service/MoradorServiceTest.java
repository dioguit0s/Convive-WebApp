package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Repository.MoradorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoradorServiceTest {
    @Mock
    private MoradorRepository moradorRepository;
    @Mock
    private UsuarioService usuarioServiceMock;
    @InjectMocks
    private MoradorService moradorService;

    @Test
    void moradorService_Insert_Sucesso() {
        Morador morador = new Morador();
        when(moradorRepository.save(morador)).thenReturn(morador);
        doNothing().when(usuarioServiceMock).prepareToInsertUser(morador);

        Morador salvo = moradorService.insert(morador);

        assertFalse(salvo.isInadimplente());
        verify(moradorRepository, times(1)).save(morador);
    }

    @Test
    void moradorService_SearchById_ErroNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(moradorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> moradorService.searchById(id));
    }
}

