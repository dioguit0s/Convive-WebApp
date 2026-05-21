package com.EC6.Convive.Controller;

import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.DashboardService;
import com.EC6.Convive.dto.DashboardDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/dashboard")
public class DashboardModeradorController {

    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(@RequestParam(required = false) String mes,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        DashboardDataDto data = dashboardService.buildDashboard(mes);

        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("dashboard", data);
        model.addAttribute("chartOcorrencias", data.getChartOcorrenciasStatus());
        model.addAttribute("chartReservas", data.getChartReservasPorArea());
        model.addAttribute("chartComunicados", data.getChartComunicadosPorTipo());

        return "moderador/dashboardOperacional";
    }
}
