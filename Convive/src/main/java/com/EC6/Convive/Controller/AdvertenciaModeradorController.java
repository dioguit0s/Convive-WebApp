package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.GravidadeNotificacao;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.MoradorService;
import com.EC6.Convive.Service.NotificacaoService;
import com.EC6.Convive.Util.PaginationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/advertencias")
public class AdvertenciaModeradorController {

    private final NotificacaoService notificacaoService;
    private final MoradorService moradorService;

    @GetMapping("/nova")
    public String formulario(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("gravidades", GravidadeNotificacao.values());
        return "moderador/novaAdvertencia";
    }

    @GetMapping("/moradores")
    public String buscarMoradores(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, PaginationConstants.DEFAULT_PAGE_SIZE);
        Page<Morador> moradoresPage = moradorService.findPaginatedAndFiltered(q, pageable);
        model.addAttribute("moradores", moradoresPage.getContent());
        model.addAttribute("hasMore", moradoresPage.hasNext());
        model.addAttribute("paginaAtual", page);
        return "moderador/novaAdvertencia :: moradorOpcoes";
    }

    @PostMapping("/nova")
    public String salvar(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @RequestParam UUID moradorId,
                         @RequestParam String titulo,
                         @RequestParam String descricao,
                         @RequestParam GravidadeNotificacao gravidade,
                         @RequestParam(defaultValue = "false") boolean gerouMulta,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataOcorrencia,
                         RedirectAttributes attributes) {
        if (!(userDetails.getUsuario() instanceof Moderador moderador)) {
            attributes.addFlashAttribute("erro", "Apenas moderadores podem emitir advertências.");
            return "redirect:/moderador/dashboard";
        }

        Morador morador = moradorService.searchById(moradorId);

        Notificacao notificacao = new Notificacao();
        notificacao.setEmitidoPor(moderador);
        notificacao.setMorador(morador);
        notificacao.setApartamento(morador.getApartamento());
        notificacao.setTitulo(titulo);
        notificacao.setDescricao(descricao);
        notificacao.setGravidade(gravidade);
        notificacao.setGerouMulta(gerouMulta);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacao.setDataOcorrencia(dataOcorrencia != null ? dataOcorrencia : LocalDateTime.now());

        notificacaoService.insert(notificacao);

        attributes.addFlashAttribute("sucesso", "Advertência registrada com sucesso!");
        return "redirect:/moderador/dashboard";
    }
}
