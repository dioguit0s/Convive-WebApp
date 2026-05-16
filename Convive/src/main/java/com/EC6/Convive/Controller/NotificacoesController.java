package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/notificacoes")
public class NotificacoesController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public String listarNotificacoes(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Usuario usuario = userDetails.getUsuario();

        List<Notificacao> notificacoes = notificacaoService.listAllByUserId(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("notificacoes", notificacoes);
        return "morador/notificacoes";
    }

}
