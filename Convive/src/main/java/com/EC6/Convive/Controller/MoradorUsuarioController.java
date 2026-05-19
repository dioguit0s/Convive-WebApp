package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Service.MoradorService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/gestaoMoradores") // Mantemos esta como a rota raiz de domínio
public class MoradorUsuarioController {

    private final UsuarioService usuarioService;
    private final MoradorService moradorService;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> moradorPage = usuarioService.findPaginatedAndFiltered(search, pageable);

        model.addAttribute("moradorPage", moradorPage);
        model.addAttribute("search", search);

        return "moderador/gestaoMoradores";
    }

    // NOVO: Necessário para que o botão "Novo Cadastro" consiga carregar a página
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("morador", new Morador());
        return "moderador/cadastro-morador";
    }

    @PostMapping("/novo") // Rota simplificada
    public String cadastrarMorador(@ModelAttribute Morador morador, RedirectAttributes redirectAttributes) {
        try {
            moradorService.insert(morador);
            redirectAttributes.addFlashAttribute("sucesso", "Morador cadastrado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        // Correção: Volta para a listagem onde os alertas (Flash Attributes) aparecem
        return "redirect:/moderador/gestaoMoradores";
    }

    @PostMapping("/editar") // Rota simplificada
    public String editarMorador(@ModelAttribute Morador morador, RedirectAttributes redirectAttributes) {
        try {
            moradorService.update(morador.getId(), morador);
            redirectAttributes.addFlashAttribute("sucesso", "Morador atualizado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        // Correção: o redirect estava apontando para gestaoUsuarios, que não existe
        return "redirect:/moderador/gestaoMoradores";
    }
}