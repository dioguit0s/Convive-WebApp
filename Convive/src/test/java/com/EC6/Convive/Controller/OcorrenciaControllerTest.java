package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.CategoriaOcorrencia;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.FileStorageService;
import com.EC6.Convive.Service.OcorrenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OcorrenciaControllerTest {

    @Mock
    private OcorrenciaService ocorrenciaService;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private OcorrenciaController ocorrenciaController;

    @Test
    void excluirOcorrencia_ServicoConfirma_Sucesso() {
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        UUID id = UUID.randomUUID();
        when(ocorrenciaService.deleteForUser(id, morador)).thenReturn(true);

        String view = ocorrenciaController.excluirOcorrencia(userDetails, id, redirectAttributes);

        assertEquals("redirect:/morador/ocorrencias", view);
        verify(ocorrenciaService, times(1)).deleteForUser(id, morador);
        verify(redirectAttributes).addFlashAttribute("sucessoOcorrencia", "Ocorrência eliminada com sucesso.");
        verify(redirectAttributes, never()).addFlashAttribute(eq("erroOcorrencia"), any());
    }

    @Test
    void excluirOcorrencia_NaoPertenceAoUsuario_Falha() {
        // Reproduz o cenário do IDOR corrigido na issue #72: o service nega a
        // exclusão de uma ocorrência que não pertence ao morador autenticado
        // (nem é moderador), e o controller precisa refletir isso sem excluir nada.
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        UUID id = UUID.randomUUID();
        when(ocorrenciaService.deleteForUser(id, morador)).thenReturn(false);

        String view = ocorrenciaController.excluirOcorrencia(userDetails, id, redirectAttributes);

        assertEquals("redirect:/morador/ocorrencias", view);
        verify(ocorrenciaService, times(1)).deleteForUser(id, morador);
        verify(redirectAttributes).addFlashAttribute("erroOcorrencia", "Não foi possível eliminar esta ocorrência.");
        verify(redirectAttributes, never()).addFlashAttribute(eq("sucessoOcorrencia"), any());
    }

    @Test
    void registrarNovaOcorrencia_TituloVazio_NaoRegistra() {
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        MockMultipartFile semAnexo = new MockMultipartFile("imagemEvidencia", new byte[0]);

        String view = ocorrenciaController.registrarNovaOcorrencia(
                userDetails, "  ", CategoriaOcorrencia.OUTRO, "descricao", semAnexo, redirectAttributes);

        assertEquals("redirect:/morador/ocorrencias", view);
        verify(ocorrenciaService, never()).insert(any());
        verify(redirectAttributes).addFlashAttribute("erroOcorrencia", "O título é obrigatório.");
    }

    @Test
    void registrarNovaOcorrencia_Sucesso() throws Exception {
        Morador morador = new Morador();
        CustomUserDetails userDetails = new CustomUserDetails(morador);
        MockMultipartFile semAnexo = new MockMultipartFile("imagemEvidencia", new byte[0]);

        String view = ocorrenciaController.registrarNovaOcorrencia(
                userDetails, "Vazamento", CategoriaOcorrencia.INFRAESTRUTURA, "descricao", semAnexo, redirectAttributes);

        assertEquals("redirect:/morador/ocorrencias", view);
        verify(ocorrenciaService, times(1)).insert(any());
        verify(redirectAttributes).addFlashAttribute("sucessoOcorrencia", "Ocorrência registrada com sucesso!");
        verify(fileStorageService, never()).salvarImagemOcorrencia(any());
    }
}
