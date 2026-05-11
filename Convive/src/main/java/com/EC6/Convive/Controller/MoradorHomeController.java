package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.ComunicadoService;
import com.EC6.Convive.Service.ReservaService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador")
public class MoradorHomeController {

    private final ComunicadoService comunicadoService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @GetMapping("/home")
    public String dashboardMorador(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();

        List<Comunicado> comunicados = comunicadoService.listAll();

        List<Reserva> reservas = reservaService.listByUser(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("comunicados", comunicados);
        model.addAttribute("reservas", reservas);

        return "morador/home";
    }
}