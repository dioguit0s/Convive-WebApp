package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Repository.ModeradorRepository;
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
public class ModeradorServiceTest {

    @Mock private ModeradorRepository moderadorRepository;
    @Mock private UsuarioService usuarioService;
    @InjectMocks private ModeradorService moderadorService;

    @Test
    void moderadorService_Insert_Sucesso() {
        Moderador moderador = new Moderador();
        moderador.setEmail("admin@convive.com");

        doNothing().when(usuarioService).prepareToInsertUser(moderador);
        when(moderadorRepository.save(moderador)).thenReturn(moderador);

        Moderador salvo = moderadorService.insert(moderador);

        assertNotNull(salvo);
        verify(usuarioService, times(1)).prepareToInsertUser(moderador);
        verify(moderadorRepository, times(1)).save(moderador);
    }

    @Test
    void moderadorService_SearchById_ErroNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(moderadorRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> moderadorService.searchById(id));
        assertEquals("Moderador não encontrada com o ID: " + id, exception.getMessage());
    }
}
