package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.AreaComumService;
import com.EC6.Convive.Service.ComunicadoService;
import com.EC6.Convive.Service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoradorHomeControllerTest {
    @Mock
    private ComunicadoService comunicadoService;
    @Mock private ReservaService reservaService;
    @Mock private AreaComumService areaComumService;
    @Mock private Model model;
    @InjectMocks
    private MoradorHomeController moradorHomeController;

    private CustomUserDetails userDetails;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Morador();
        usuarioMock.setId(UUID.randomUUID());
        userDetails = new CustomUserDetails(usuarioMock);
    }

    @Test
    void moradorHomeController_Dashboard_Sucesso() {
        when(comunicadoService.listAll()).thenReturn(List.of(new Comunicado()));
        when(reservaService.listByUser(usuarioMock.getId())).thenReturn(List.of(new Reserva()));

        String view = moradorHomeController.dashboardMorador(userDetails, model);

        assertEquals("morador/home", view);
        verify(model).addAttribute(eq("usuario"), any(Usuario.class));
        verify(model).addAttribute(eq("comunicados"), anyList());
        verify(model).addAttribute(eq("reservas"), anyList());
    }

}
