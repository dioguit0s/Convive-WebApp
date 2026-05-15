package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void usuarioService_PrepareToInsert_Sucesso() {
        Usuario usuario = new Morador();
        usuario.setEmail("teste@convive.com");
        usuario.setSenhaHash("123");

        when(usuarioRepository.existsByEmail("teste@convive.com")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hash123");

        usuarioService.prepareToInsertUser(usuario);

        assertEquals("Ativo", usuario.getStatus());
        assertEquals("hash123", usuario.getSenhaHash());
    }

    @Test
    void usuarioService_PrepareToInsert_ErroEmailExistente() {
        Usuario usuario = new Morador();
        usuario.setEmail("existente@convive.com");

        when(usuarioRepository.existsByEmail("existente@convive.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.prepareToInsertUser(usuario);
        });
        assertEquals("Email já cadastrado no sistema", exception.getMessage());
    }
}
