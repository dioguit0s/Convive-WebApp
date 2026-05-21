package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.ComunicadoRepository;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.ComunicadoService;
import com.EC6.Convive.Service.FileStorageService;
import com.EC6.Convive.Util.PaginationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/comunicados")
public class ComunicadoController {

    private final ComunicadoService comunicadoService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String listarComunicados(@RequestParam(defaultValue = "0") int page,
                                    @AuthenticationPrincipal CustomUserDetails userDetails , Model model) {

        Page<Comunicado> comunicadosPage = comunicadoService.findPaginated(page, PaginationConstants.DEFAULT_PAGE_SIZE);

        model.addAttribute("comunicados", comunicadosPage.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", comunicadosPage.getTotalPages());

        return "morador/comunicados";
    }

    @PostMapping
    public String criarComunicado(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @ModelAttribute Comunicado comunicado,
                                  @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                                  RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();

        if (usuario instanceof Moderador) {
            comunicado.setModerador((Moderador) usuario);
            comunicado.setPublicadoEm(LocalDateTime.now());

            try {
                // Lógica de salvamento da imagem
                if (imagem != null && !imagem.isEmpty()) {
                    String urlImagem = fileStorageService.salvarImagemComunicado(imagem);
                    comunicado.setUrlImagem(urlImagem);
                }

                comunicadoService.insert(comunicado);
                redirectAttributes.addFlashAttribute("sucesso", "Aviso publicado com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", "Erro ao publicar aviso: " + e.getMessage());
            }

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

    @GetMapping("/mais")
    public String carregarMais(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Comunicado> comunicadosPage = comunicadoService.findPaginated(page, PaginationConstants.DEFAULT_PAGE_SIZE);
        model.addAttribute("comunicados", comunicadosPage.getContent());

        // O segredo está aqui: o Spring retornará apenas o fragmento 'listagemCards'
        return "morador/comunicados :: listagemCards";
    }

}
