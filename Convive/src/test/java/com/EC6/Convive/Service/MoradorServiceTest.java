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
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void moradorService_Update_PersisteInadimplente() {
        UUID id = UUID.randomUUID();
        Morador existente = new Morador();
        existente.setId(id);
        existente.setInadimplente(true);

        Morador atualizado = new Morador();
        atualizado.setNome("Maria");
        atualizado.setEmail("maria@test.com");
        atualizado.setApartamento(202);
        atualizado.setInadimplente(false);

        when(moradorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(moradorRepository.save(any(Morador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Morador salvo = moradorService.update(id, atualizado);

        assertFalse(salvo.isInadimplente());
        verify(moradorRepository).save(existente);
    }
}

