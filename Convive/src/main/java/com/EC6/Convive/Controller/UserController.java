package com.EC6.Convive.Controller;


import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.UsuarioRepository;
import com.EC6.Convive.Service.FileStorageService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class UserController {

    private final FileStorageService fileStorageService;
    private final UsuarioService usuarioService;


    @PostMapping("/upload-foto")
    public String uploadFotoPerfil(@RequestParam("foto") MultipartFile foto, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.getByEmail(principal.getName());

            String caminhoImagem = fileStorageService.salvarImagemPerfil(foto);

            if (caminhoImagem != null) {
                usuario.setFotoPerfil(caminhoImagem);
                usuarioService.update(usuario.getId(), usuario);
            }

            redirectAttributes.addFlashAttribute("sucesso", "Foto de perfil atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao fazer upload da foto de perfil.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            boolean isModerador = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"));

            if (isModerador) {
                return "redirect:/moderador/dashboard";
            }

            return "redirect:/morador/home";

    }
}
