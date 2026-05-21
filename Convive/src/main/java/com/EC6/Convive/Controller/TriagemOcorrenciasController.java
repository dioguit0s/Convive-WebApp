package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
import com.EC6.Convive.Model.StatusOcorrencia;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.OcorrenciaService;
import com.EC6.Convive.Service.UsuarioService;
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
@RequestMapping("/moderador/triagemOcorrencias")
public class TriagemOcorrenciasController {

    private final OcorrenciaService ocorrenciaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String gerenciarOcorrencias(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuarioLogado = usuarioService.getByEmail(userDetails.getUsername());
        model.addAttribute("usuario", usuarioLogado);
        List<Ocorrencia> ocorrencias = ocorrenciaService.listAll();
        model.addAttribute("ocorrencias", ocorrencias);
        return "moderador/triagemOcorrencias";
    }

    @PostMapping("/atualizar")
    public String atualizarOcorrencia(@RequestParam UUID id,
                                      @RequestParam Prioridade prioridade,
                                      @RequestParam StatusOcorrencia status,
                                      @RequestParam(required = false) String respostaModerador,
                                      RedirectAttributes attributes) {
        Ocorrencia ocorrencia = ocorrenciaService.searchById(id);

        ocorrencia.setPrioridade(prioridade);
        ocorrencia.setStatus(status);
        if (respostaModerador != null && !respostaModerador.isBlank()) {
            ocorrencia.setRespostaModerador(respostaModerador);
        }

        ocorrenciaService.update(ocorrencia);

        attributes.addFlashAttribute("sucesso", "Ocorrência atualizada com sucesso!");
        return "redirect:/moderador/triagemOcorrencias";
    }
}