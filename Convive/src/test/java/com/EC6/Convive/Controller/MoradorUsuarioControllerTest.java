package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Service.MoradorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoradorUsuarioControllerTest {
    @Mock
    private MoradorService moradorService;
    @Mock private RedirectAttributes redirectAttributes;
    @InjectMocks
    private MoradorUsuarioController moradorUsuarioController;

    @Test
    void moradorUsuarioController_CadastrarMorador_Sucesso() {
        Morador morador = new Morador();
        when(moradorService.insert(morador)).thenReturn(morador);

        String view = moradorUsuarioController.cadastrarMorador(morador, redirectAttributes);

        assertEquals("redirect:/moderador/usuarios/novo", view);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Morador cadastrado com sucesso!");
    }

    @Test
    void moradorUsuarioController_CadastrarMorador_Erro() {
        Morador morador = new Morador();
        when(moradorService.insert(morador)).thenThrow(new RuntimeException("Email duplicado"));

        String view = moradorUsuarioController.cadastrarMorador(morador, redirectAttributes);

        assertEquals("redirect:/moderador/usuarios/novo", view);
        verify(redirectAttributes).addFlashAttribute("erro", "Email duplicado");
    }
}
