package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Service.MoradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/usuarios")
public class MoradorUsuarioController {

    private final MoradorService moradorService;

    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("morador", new Morador());
        return "moderador/cadastro-morador";
    }

    @PostMapping("/novo")
    public String cadastrarMorador(@ModelAttribute Morador morador, RedirectAttributes redirectAttributes) {
        try {
            moradorService.insert(morador);
            redirectAttributes.addFlashAttribute("sucesso", "Morador cadastrado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/moderador/usuarios/novo";
    }
}
