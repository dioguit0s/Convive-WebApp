package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.StatusArea;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.AreaComumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/areasComuns")
public class AreaComumModeradorController {

    private final AreaComumService areaComumService;

    @GetMapping
    public String listar(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("areas", areaComumService.listAll());
        model.addAttribute("statusOptions", StatusArea.values());
        return "moderador/areasComuns";
    }

    @PostMapping("/nova")
    public String cadastrar(@RequestParam String nome,
                            @RequestParam int capacidade,
                            @RequestParam(defaultValue = "ATIVA") StatusArea statusArea,
                            RedirectAttributes attributes) {
        try {
            if (capacidade < 1) {
                throw new IllegalArgumentException("A capacidade deve ser de pelo menos 1 pessoa.");
            }
            areaComumService.criar(nome, capacidade, statusArea);
            attributes.addFlashAttribute("sucesso", "Área \"" + nome.trim() + "\" cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/moderador/areasComuns";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable UUID id,
                         @RequestParam String nome,
                         @RequestParam int capacidade,
                         @RequestParam StatusArea statusArea,
                         RedirectAttributes attributes) {
        try {
            if (capacidade < 1) {
                throw new IllegalArgumentException("A capacidade deve ser de pelo menos 1 pessoa.");
            }
            var area = areaComumService.atualizar(id, nome, capacidade, statusArea);
            attributes.addFlashAttribute("sucesso", "Área \"" + area.getNome() + "\" atualizada com sucesso!");
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("erro", "Área não encontrada.");
        }
        return "redirect:/moderador/areasComuns";
    }
}
