package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.ComunicadoRepository;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.ComunicadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/comunicados")
public class ComunicadoController {

    private final ComunicadoService comunicadoService;

    @GetMapping
    public String listarComunicados(@AuthenticationPrincipal CustomUserDetails userDetails , Model model) {

        Usuario usuario = userDetails.getUsuario();

        List<Comunicado> comunicados = comunicadoService.findAllOrderByDate();

        model.addAttribute("usuario", usuario);
        model.addAttribute("comunicados", comunicados);
        return "morador/comunicados";
    }

    @PostMapping
    public String criarComunicado(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @ModelAttribute Comunicado comunicado,
                                  RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();

        if (usuario instanceof Moderador) {
            comunicado.setModerador((Moderador) usuario);
            comunicado.setPublicadoEm(LocalDateTime.now());

            comunicadoService.insert(comunicado);
            redirectAttributes.addFlashAttribute("sucesso", "Aviso publicado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para publicar comunicados.");
        }

        return "redirect:/morador/comunicados";
    }

    @PostMapping("/excluir")
    public String excluirComunicado(@RequestParam UUID id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            comunicadoService.delete(id);
            redirectAttributes.addFlashAttribute("sucesso", "Aviso excluído com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir o aviso.");
        }
        return "redirect:/morador/comunicados";
    }
}
