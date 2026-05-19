package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.FileStorageService;
import com.EC6.Convive.Service.OcorrenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String listarMinhasOcorrencias(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();

        // Busca apenas as ocorrências do morador logado
        List<Ocorrencia> ocorrencias = ocorrenciaService.listByUser(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("ocorrencias", ocorrencias);
        return "morador/ocorrencias";
    }

    @PostMapping
    public String registrarNovaOcorrencia(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String descricao,
            @RequestParam(value = "imagemEvidencia", required = false) MultipartFile imagemEvidencia, // NOVO
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuarioLogado = userDetails.getUsuario();

            Ocorrencia novaOcorrencia = new Ocorrencia();
            novaOcorrencia.setDescricao(descricao);
            novaOcorrencia.setPrioridade(Prioridade.NAO_DEFINIDA);
            novaOcorrencia.setStatus(StatusOcorrencia.REGISTRADA);
            novaOcorrencia.setUsuario(usuarioLogado);

            if (imagemEvidencia != null && !imagemEvidencia.isEmpty()) {
                String urlImagem = fileStorageService.salvarImagemOcorrencia(imagemEvidencia);
                novaOcorrencia.setUrlEvidencia(urlImagem);
            }

            ocorrenciaService.insert(novaOcorrencia);
            redirectAttributes.addFlashAttribute("sucessoOcorrencia", "Ocorrência registrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroOcorrencia", "Erro ao registrar: " + e.getMessage());
        }
        return "redirect:/morador/ocorrencias";
    }

    @PostMapping("/excluir")
    public String excluirOcorrencia(@RequestParam UUID id, RedirectAttributes redirectAttributes) {
        try {
            ocorrenciaService.delete(id);
            redirectAttributes.addFlashAttribute("sucessoOcorrencia", "Ocorrência eliminada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroOcorrencia", "Erro ao eliminar a ocorrência.");
        }
        return "redirect:/morador/ocorrencias";
    }
}