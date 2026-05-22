package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.*;
import com.EC6.Convive.Util.PaginationConstants;
import com.EC6.Convive.Validator.MoradorHomeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final MoradorHomeValidator moradorHomeValidator;

    @GetMapping("/home")
    public String dashboardMorador(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.getByEmail(userDetails.getUsername());

        List<Comunicado> comunicados = comunicadoService.findTopPublished(PaginationConstants.HOME_PREVIEW_SIZE);
        List<Reserva> reservas = reservaService.findTopByUser(usuario.getId(), PaginationConstants.HOME_PREVIEW_SIZE);

        model.addAttribute("usuario", usuario);
        model.addAttribute("comunicados", comunicados);
        model.addAttribute("reservas", reservas);

        return "morador/home";
    }

    @GetMapping("/reservas")
    public String listarReservasMorador(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Usuario usuario = usuarioService.getByEmail(userDetails.getUsername());

        Page<Reserva> reservasPage = reservaService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE);
        List<AreaComum> areasComuns = areaComumService.listAll().stream()
                .sorted(Comparator.comparing(AreaComum::getNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        boolean temAreaAtiva = areasComuns.stream().anyMatch(a -> a.getStatusArea() == StatusArea.ATIVA);

        model.addAttribute("usuario", usuario);
        model.addAttribute("reservas", reservasPage.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", reservasPage.getTotalPages());
        model.addAttribute("listaVazia", reservasPage.getTotalElements() == 0);
        model.addAttribute("areasComuns", areasComuns);
        model.addAttribute("temAreaAtiva", temAreaAtiva);
        return "morador/reservas";
    }

    @GetMapping("/reservas/mais")
    public String carregarMaisReservas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        Usuario usuario = usuarioService.getByEmail(userDetails.getUsername());
        Page<Reserva> reservasPage = reservaService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE);
        model.addAttribute("reservas", reservasPage.getContent());
        return "morador/reservas :: listagemReservasSource";
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
        String erroValidacao = moradorHomeValidator.validarNovaReserva(usuario, areaId, dataReserva, turno, convidadosEstimados);

        if (erroValidacao != null) {
            redirectAttributes.addFlashAttribute("erroReserva", erroValidacao);
            return "redirect:/morador/reservas";
        }

        AreaComum area = areaComumService.searchById(areaId);
        turno = turno.toLowerCase(Locale.ROOT);

        Integer convidados = null;
        if (convidadosEstimados != null && !convidadosEstimados.isBlank()) {
            convidados = Integer.parseInt(convidadosEstimados.trim());
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

    @PostMapping("/usuarios/{id}/inadimplencia")
    public String alterarInadimplenciaUsuario(
            @PathVariable UUID id,
            @RequestParam boolean inadimplente,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.alterarInadimplencia(id, inadimplente);

            if (inadimplente) {
                redirectAttributes.addFlashAttribute("sucesso", "Morador marcado como INADIMPLENTE com sucesso.");
            } else {
                redirectAttributes.addFlashAttribute("sucesso", "Restrição removida. Morador agora está ADIMPLENTE.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao tentar alterar o status do morador.");
        }

        return "redirect:/moderador/usuarios";
    }
}