package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Service.ModeradorService;
import com.EC6.Convive.Service.MoradorService;
import com.EC6.Convive.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoradorUsuarioControllerTest {
    @Mock
    private MoradorService moradorService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ModeradorService moderadorService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private MoradorUsuarioController moradorUsuarioController;

    @Test
    void moradorUsuarioController_CadastrarMorador_Sucesso() {
        Morador morador = new Morador();
        when(moradorService.insert(any(Morador.class))).thenReturn(morador);

        String view = moradorUsuarioController.cadastrarUsuario(
                "João", "joao@test.com", "senha123", "MORADOR", 101, redirectAttributes);

        assertEquals("redirect:/moderador/gestaoMoradores", view);
        verify(moradorService).insert(any(Morador.class));
        verify(redirectAttributes).addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
    }

    @Test
    void moradorUsuarioController_CadastrarMorador_Erro() {
        when(moradorService.insert(any(Morador.class))).thenThrow(new RuntimeException("Email duplicado"));

        String view = moradorUsuarioController.cadastrarUsuario(
                "João", "joao@test.com", "senha123", "MORADOR", null, redirectAttributes);

        assertEquals("redirect:/moderador/gestaoMoradores", view);
        verify(redirectAttributes).addFlashAttribute("erro", "Email duplicado");
    }
}
