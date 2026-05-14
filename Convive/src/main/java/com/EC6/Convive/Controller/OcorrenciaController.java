package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.OcorrenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

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
            @RequestParam Prioridade prioridade,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuarioLogado = userDetails.getUsuario(); // Pega o usuário genérico (Morador ou Moderador)

            Ocorrencia novaOcorrencia = new Ocorrencia();
            novaOcorrencia.setDescricao(descricao);
            novaOcorrencia.setPrioridade(prioridade);
            novaOcorrencia.setStatus(StatusOcorrencia.REGISTRADA);
            novaOcorrencia.setUsuario(usuarioLogado); // Atribui o usuário logado sem necessidade de cast

            ocorrenciaService.insert(novaOcorrencia);
            redirectAttributes.addFlashAttribute("sucessoOcorrencia", "Ocorrência registrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroOcorrencia", "Erro ao registrar: " + e.getMessage());
        }
        return "redirect:/morador/ocorrencias";
    }
}