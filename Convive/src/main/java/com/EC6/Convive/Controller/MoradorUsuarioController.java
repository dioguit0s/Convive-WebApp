package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Service.ModeradorService;
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

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/gestaoMoradores") // Mantemos esta como a rota raiz de domínio
public class MoradorUsuarioController {

    private final UsuarioService usuarioService;
    private final MoradorService moradorService;
    private final ModeradorService moderadorService;

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

    @PostMapping("/novo")
    public String cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String tipo,
            @RequestParam(required = false) Integer apartamento,
            RedirectAttributes redirectAttributes) {
        try {
            if ("MODERADOR".equalsIgnoreCase(tipo)) {
                Moderador moderador = new Moderador();
                moderador.setNome(nome);
                moderador.setEmail(email);
                moderador.setSenhaHash(senha);
                moderador.setApartamento(apartamento);
                moderadorService.insert(moderador);
            } else {
                Morador morador = new Morador();
                morador.setNome(nome);
                morador.setEmail(email);
                morador.setSenhaHash(senha);
                if (apartamento != null) morador.setApartamento(apartamento);
                moradorService.insert(morador);
            }
            redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/moderador/gestaoMoradores";
    }

    @PostMapping("/editar")
    public String editarUsuario(
            @RequestParam UUID id,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) Integer apartamento,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario user = usuarioService.searchById(id);

            if (user instanceof Morador) {
                Morador m = (Morador) user;
                m.setNome(nome);
                m.setEmail(email);
                if (apartamento != null) m.setApartamento(apartamento);
                moradorService.update(id, m);
            } else if (user instanceof Moderador) {
                Moderador m = (Moderador) user;
                m.setNome(nome);
                m.setEmail(email);
                m.setApartamento(apartamento);
                moderadorService.update(id, m);
            }
            redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/moderador/gestaoMoradores";
    }

    @PostMapping("/excluir")
    public String excluirUsuario(@RequestParam UUID id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir o usuário. Certifique-se de que ele não possui pendências ou vínculos ativos no sistema.");
        }
        return "redirect:/moderador/gestaoMoradores";
    }
}