package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Model.StatusReserva;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.Service.ReservaService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/triagemReservas")
public class TriagemReservasController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final ContactMailService contactMailService;

    @GetMapping
    public String gerenciarReservas(Model model) {
        List<Reserva> reservas = reservaService.listAll();
        model.addAttribute("reservas", reservas);
        return "moderador/triagemReservas";
    }

    @PostMapping("/aprovar")
    public String aprovarReserva(@RequestParam UUID id, RedirectAttributes attributes) {
        Reserva reserva = reservaService.searchById(id);
        reserva.setStatus(StatusReserva.APROVADO);
        reservaService.insert(reserva); // Atualiza no banco
        attributes.addFlashAttribute("sucesso", "Reserva aprovada com sucesso!");
        return "redirect:/moderador/triagemReservas";
    }

    @PostMapping("/rejeitar")
    public String rejeitarReserva(@RequestParam UUID id, @RequestParam String motivo, RedirectAttributes attributes) {
        Reserva reserva = reservaService.searchById(id);
        reserva.setStatus(StatusReserva.REPROVADO);
        reserva.setMotivoRejeicao(motivo);
        reservaService.insert(reserva); // Atualiza no banco

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM ");
        //envia email para o usuario
        String assunto = "Reserva rejeitada";
        String corpo = """
                Sua reserva de area comum foi rejeitada para o dia: %s

                Mensagem do moderador:
                %s.
                
                Verifique o seu portal para mais informacoes
                """.formatted(reserva.getInicio().format(formatter), reserva.getMotivoRejeicao());
        Usuario usuario = usuarioService.searchById(reserva.getReservadoPor().getId());
        String email = usuario.getEmail();
        ContactMessageModel mensagem = new ContactMessageModel();
        mensagem.setEmail(email);
        mensagem.setSubject(assunto);
        mensagem.setMessage(corpo);

        try {
            //contactMailService.sendToOutside(mensagem);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de rejeição: " + e.getMessage());
        }
        attributes.addFlashAttribute("sucesso", "Reserva rejeitada com sucesso!");
        return "redirect:/moderador/triagemReservas";
    }
}

