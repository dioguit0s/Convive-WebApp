package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.*;
import lombok.RequiredArgsConstructor;
import com.EC6.Convive.Event.ReservaPendenteCriadaEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador")
public class MoradorHomeController {

    private static final ZoneId ZONA_APP = ZoneId.of("America/Sao_Paulo");
    private static final Set<String> TURNOS_VALIDOS = Set.of("manha", "tarde", "noite", "integral");

    private final ComunicadoService comunicadoService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final AreaComumService areaComumService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/home")
    public String dashboardMorador(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();

        List<Comunicado> comunicados = comunicadoService.findAllOrderByDate();

        List<Reserva> reservas = reservaService.listByUser(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("comunicados", comunicados);
        model.addAttribute("reservas", reservas);

        return "morador/home";
    }

    @GetMapping("/reservas")
    public String listarReservasMorador(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        List<Reserva> reservas = reservaService.listByUser(usuario.getId()).stream()
                .sorted(Comparator.comparing(Reserva::getInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        List<AreaComum> areasComuns = areaComumService.listAll();
        boolean temAreaAtiva = areasComuns.stream().anyMatch(a -> a.getStatusArea() == StatusArea.ATIVA);
        model.addAttribute("usuario", usuario);
        model.addAttribute("reservas", reservas);
        model.addAttribute("areasComuns", areasComuns);
        model.addAttribute("temAreaAtiva", temAreaAtiva);
        return "morador/reservas";
    }

    @PostMapping("/reservas")
    public String criarReservaMorador(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam UUID areaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReserva,
            @RequestParam String turno,
            @RequestParam(required = false) String convidadosEstimados,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        if (turno == null || !TURNOS_VALIDOS.contains(turno.toLowerCase(Locale.ROOT))) {
            redirectAttributes.addFlashAttribute("erroReserva", "Turno inválido.");
            return "redirect:/morador/reservas";
        }
        turno = turno.toLowerCase(Locale.ROOT);

        LocalDate hoje = LocalDate.now(ZONA_APP);
        if (dataReserva.isBefore(hoje)) {
            redirectAttributes.addFlashAttribute("erroReserva", "A data não pode ser no passado.");
            return "redirect:/morador/reservas";
        }

        AreaComum area;
        try {
            area = areaComumService.searchById(areaId);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("erroReserva", "Ambiente não encontrado.");
            return "redirect:/morador/reservas";
        }

        if (area.getStatusArea() != StatusArea.ATIVA) {
            redirectAttributes.addFlashAttribute("erroReserva", "Este ambiente não está disponível para reserva.");
            return "redirect:/morador/reservas";
        }

        Integer convidados = null;
        if (convidadosEstimados != null && !convidadosEstimados.isBlank()) {
            try {
                convidados = Integer.parseInt(convidadosEstimados.trim());
            } catch (NumberFormatException ex) {
                redirectAttributes.addFlashAttribute("erroReserva", "Número de convidados inválido.");
                return "redirect:/morador/reservas";
            }
        }

        if (convidados != null) {
            if (convidados < 1) {
                redirectAttributes.addFlashAttribute("erroReserva", "O número de convidados deve ser pelo menos 1.");
                return "redirect:/morador/reservas";
            }
            if (convidados > area.getCapacidade()) {
                redirectAttributes.addFlashAttribute("erroReserva",
                        "O número de convidados não pode exceder a capacidade do ambiente (" + area.getCapacidade() + ").");
                return "redirect:/morador/reservas";
            }
        }

        LocalDateTime[] periodo = resolverPeriodoTurno(dataReserva, turno);
        LocalDateTime inicio = periodo[0];
        LocalDateTime fim = periodo[1];

        Reserva reserva = new Reserva();
        reserva.setReservadoPor(usuario);
        reserva.setAreaReservada(area);
        reserva.setInicio(inicio);
        reserva.setFim(fim);
        reserva.setConvidadosEstimados(convidados);
        if (observacoes != null) {
            String obs = observacoes.trim();
            if (obs.length() > 2000) {
                obs = obs.substring(0, 2000);
            }
            reserva.setObservacoes(obs.isEmpty() ? null : obs);
        }

        if (reservaService.canAutoApproveNewBooking(usuario.getId(), area.getId(), inicio, fim)) {
            reserva.setStatus(StatusReserva.APROVADO);
            redirectAttributes.addFlashAttribute("sucessoReserva",
                    "Reserva registrada e aprovada automaticamente.");
        } else {
            reserva.setStatus(StatusReserva.PENDENTE);
            redirectAttributes.addFlashAttribute("sucessoReserva",
                    "Sua solicitação de reserva foi registrada e está pendente de aprovação.");
        }

        reservaService.insert(reserva);

        if (reserva.getStatus() == StatusReserva.PENDENTE) {
            eventPublisher.publishEvent(new ReservaPendenteCriadaEvent(
                    reserva.getAreaReservada().getNome(),
                    reserva.getReservadoPor().getNome(),
                    reserva.getInicio(),
                    reserva.getObservacoes()
            ));
        }

        return "redirect:/morador/reservas";
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReservaMorador(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();
        if (reservaService.deleteForUser(id, usuario.getId())) {
            redirectAttributes.addFlashAttribute("sucessoReserva", "Reserva cancelada e removida.");
        } else {
            redirectAttributes.addFlashAttribute("erroReserva", "Não foi possível cancelar esta reserva.");
        }
        return "redirect:/morador/reservas";
    }

    private static LocalDateTime[] resolverPeriodoTurno(LocalDate data, String turno) {
        return switch (turno) {
            case "manha" -> new LocalDateTime[]{
                    LocalDateTime.of(data, LocalTime.of(8, 0)),
                    LocalDateTime.of(data, LocalTime.of(12, 0))
            };
            case "tarde" -> new LocalDateTime[]{
                    LocalDateTime.of(data, LocalTime.of(13, 0)),
                    LocalDateTime.of(data, LocalTime.of(17, 0))
            };
            case "noite" -> new LocalDateTime[]{
                    LocalDateTime.of(data, LocalTime.of(18, 0)),
                    LocalDateTime.of(data, LocalTime.of(23, 0))
            };
            case "integral" -> new LocalDateTime[]{
                    LocalDateTime.of(data, LocalTime.of(8, 0)),
                    LocalDateTime.of(data, LocalTime.of(23, 0))
            };
            default -> throw new IllegalArgumentException("Turno inválido");
        };
    }
}