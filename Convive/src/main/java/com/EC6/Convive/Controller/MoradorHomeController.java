package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Service.ComunicadoService;
import com.EC6.Convive.Service.ReservaService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
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
    public String dashboardMorador(Model model) {
        // Exemplo: Substitua pela lógica real de pegar o UUID do usuário logado via Spring Security
        UUID usuarioIdLogado = UUID.fromString("COLOQUE-UM-UUID-VALIDO-AQUI-PARA-TESTES");
        Usuario usuario = usuarioService.searchById(usuarioIdLogado);

        // Busca listas do BD
        List<Comunicado> comunicados = comunicadoService.listAll();

        // No futuro, você pode querer criar no ReservaRepository um método como:
        // List<Reserva> findByReservadoPor(Usuario usuario);
        List<Reserva> reservas = reservaService.listAll();

        // Envia para o HTML
        model.addAttribute("usuario", usuario);
        model.addAttribute("comunicados", comunicados);
        model.addAttribute("reservas", reservas);

        return "morador/home-morador"; // caminho do arquivo html
    }
}