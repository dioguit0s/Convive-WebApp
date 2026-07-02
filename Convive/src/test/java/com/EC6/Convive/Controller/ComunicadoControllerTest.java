package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.ComunicadoService;
import com.EC6.Convive.Service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComunicadoControllerTest {

    @Mock
    private ComunicadoService comunicadoService;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private ComunicadoController comunicadoController;

    @Test
    void comunicadoController_CriarComunicado_Moderador_Sucesso() {
        Moderador moderador = new Moderador();
        CustomUserDetails userDetails = new CustomUserDetails(moderador);
        Comunicado comunicado = new Comunicado();
        when(comunicadoService.insert(any(Comunicado.class))).thenReturn(comunicado);

        String view = comunicadoController.criarComunicado(userDetails, comunicado, null, redirectAttributes);

        assertEquals("redirect:/morador/comunicados", view);
        verify(comunicadoService, times(1)).insert(any(Comunicado.class));
        verify(redirectAttributes).addFlashAttribute("sucesso", "Aviso publicado com sucesso!");
        verify(redirectAttributes, never()).addFlashAttribute(eq("erro"), any());
    }

    @Test
    void comunicadoController_CriarComunicado_Moderador_Erro() {
        Moderador moderador = new Moderador();
        CustomUserDetails userDetails = new CustomUserDetails(moderador);
        Comunicado comunicado = new Comunicado();
        when(comunicadoService.insert(any(Comunicado.class))).thenThrow(new RuntimeException("Falha ao salvar"));

        String view = comunicadoController.criarComunicado(userDetails, comunicado, null, redirectAttributes);

        assertEquals("redirect:/morador/comunicados", view);
        verify(comunicadoService, times(1)).insert(any(Comunicado.class));
        verify(redirectAttributes).addFlashAttribute("erro", "Erro ao publicar aviso: Falha ao salvar");
        verify(redirectAttributes, never()).addFlashAttribute(eq("sucesso"), any());
    }

    @Test
    void comunicadoController_CriarComunicado_NaoModerador_SemPermissao() {
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        Comunicado comunicado = new Comunicado();

        String view = comunicadoController.criarComunicado(userDetails, comunicado, null, redirectAttributes);

        assertEquals("redirect:/morador/comunicados", view);
        verify(comunicadoService, never()).insert(any(Comunicado.class));
        verify(redirectAttributes).addFlashAttribute("erro", "Você não tem permissão para publicar comunicados.");
    }

    @Test
    void excluirComunicado_Moderador_Sucesso() {
        Moderador moderador = new Moderador();
        CustomUserDetails userDetails = new CustomUserDetails(moderador);
        UUID id = UUID.randomUUID();

        String view = comunicadoController.excluirComunicado(userDetails, id, redirectAttributes);

        assertEquals("redirect:/morador/comunicados", view);
        verify(comunicadoService, times(1)).delete(id);
        verify(redirectAttributes).addFlashAttribute("sucesso", "Aviso excluído com sucesso.");
        verify(redirectAttributes, never()).addFlashAttribute(eq("erro"), any());
    }

    @Test
    void excluirComunicado_NaoModerador_SemPermissao() {
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        UUID id = UUID.randomUUID();

        String view = comunicadoController.excluirComunicado(userDetails, id, redirectAttributes);

        assertEquals("redirect:/morador/comunicados", view);
        verify(comunicadoService, never()).delete(any());
        verify(redirectAttributes).addFlashAttribute("erro", "Você não tem permissão para excluir comunicados.");
    }
}
