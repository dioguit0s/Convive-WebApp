package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {
    @Mock
    private ReservaRepository reservaRepository;
    @InjectMocks
    private ReservaService reservaService;

    @Test
    void reservaService_DeleteForUser_Sucesso() {
        UUID reservaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Reserva reserva = new Reserva();

        when(reservaRepository.findByIdAndReservadoPor_Id(reservaId, usuarioId))
                .thenReturn(Optional.of(reserva));

        boolean resultado = reservaService.deleteForUser(reservaId, usuarioId);

        assertTrue(resultado);
        verify(reservaRepository, times(1)).delete(reserva);
    }

    @Test
    void reservaService_DeleteForUser_ErroNaoEncontrada() {
        UUID reservaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        when(reservaRepository.findByIdAndReservadoPor_Id(reservaId, usuarioId))
                .thenReturn(Optional.empty());

        boolean resultado = reservaService.deleteForUser(reservaId, usuarioId);

        assertFalse(resultado);
        verify(reservaRepository, never()).delete(any());
    }
}
