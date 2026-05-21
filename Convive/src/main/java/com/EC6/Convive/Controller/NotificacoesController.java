package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.NotificacaoService;
import com.EC6.Convive.Util.PaginationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/notificacoes")
public class NotificacoesController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public String listarNotificacoes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        Page<Notificacao> notificacoesPage = notificacaoService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE);

        model.addAttribute("notificacoes", notificacoesPage.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", notificacoesPage.getTotalPages());
        model.addAttribute("listaVazia", notificacoesPage.getTotalElements() == 0);
        return "morador/notificacoes";
    }

    @GetMapping("/mais")
    public String carregarMais(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        Page<Notificacao> notificacoesPage = notificacaoService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE);
        model.addAttribute("notificacoes", notificacoesPage.getContent());
        return "morador/notificacoes :: listagemCards";
    }
}
