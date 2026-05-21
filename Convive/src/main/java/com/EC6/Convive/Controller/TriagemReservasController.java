package com.EC6.Convive.Controller;

import com.EC6.Convive.Event.ReservaRejeitadaEvent;
import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Model.StatusReserva;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.ReservaService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/triagemReservas")
public class TriagemReservasController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public String gerenciarReservas(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuarioLogado = usuarioService.getByEmail(userDetails.getUsername());
        model.addAttribute("usuario", usuarioLogado);
        List<Reserva> reservas = reservaService.listAll();
        model.addAttribute("reservas", reservas);
        return "moderador/triagemReservas";
    }

    @PostMapping("/aprovar")
    public String aprovarReserva(@RequestParam UUID id, RedirectAttributes attributes) {
        Reserva reserva = reservaService.searchById(id);
        reserva.setStatus(StatusReserva.APROVADO);
        reservaService.insert(reserva);
        attributes.addFlashAttribute("sucesso", "Reserva aprovada com sucesso!");
        return "redirect:/moderador/triagemReservas";
    }

    @PostMapping("/rejeitar")
    public String rejeitarReserva(@RequestParam UUID id, @RequestParam String motivo, RedirectAttributes attributes) {
        Reserva reserva = reservaService.searchById(id);
        reserva.setStatus(StatusReserva.REPROVADO);
        reserva.setMotivoRejeicao(motivo);
        reservaService.insert(reserva);

        Usuario usuario = usuarioService.searchById(reserva.getReservadoPor().getId());
        eventPublisher.publishEvent(new ReservaRejeitadaEvent(
                usuario.getEmail(),
                reserva.getInicio(),
                reserva.getMotivoRejeicao()
        ));

        attributes.addFlashAttribute("sucesso", "Reserva rejeitada com sucesso!");
        return "redirect:/moderador/triagemReservas";
    }
}
