package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Model.StatusReserva;
import com.EC6.Convive.Repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public Reserva insert(Reserva Reserva) {
        return reservaRepository.save(Reserva);
    }

    public List<Reserva> listAll() {
        return reservaRepository.findAll();
    }

    public Reserva searchById(UUID id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        reservaRepository.deleteById(id);
    }

    public boolean deleteForUser(UUID reservaId, UUID usuarioId) {
        Optional<Reserva> reserva = reservaRepository.findByIdAndReservadoPor_Id(reservaId, usuarioId);
        if (reserva.isEmpty()) {
            return false;
        }
        reservaRepository.delete(reserva.get());
        return true;
    }

    public List<Reserva> listByUser(UUID id) {
        return reservaRepository.findByReservadoPorId(id);
    }


    public boolean areaAlreadyBooked(UUID areaId, StatusReserva status, LocalDateTime inicio, LocalDateTime fim) {
        return reservaRepository.existsByAreaReservadaIdAndStatusInAndInicioLessThanAndFimGreaterThan(
                areaId, List.of(status), fim, inicio);
    }


    public boolean userAlreadyHasBookingOnSpace(UUID usuarioId, UUID areaId, LocalDateTime inicio, LocalDateTime fim) {
        return reservaRepository.existsByReservadoPorIdAndAreaReservadaIdAndStatusInAndInicioLessThanAndFimGreaterThan(
                usuarioId, areaId, List.of(StatusReserva.PENDENTE, StatusReserva.APROVADO), fim, inicio);
    }


    public boolean canAutoApproveNewBooking(UUID usuarioId, UUID areaId, LocalDateTime inicio, LocalDateTime fim) {
        if (areaAlreadyBooked(areaId, StatusReserva.APROVADO, inicio, fim)) {
            return false;
        }
        if (userAlreadyHasBookingOnSpace(usuarioId, areaId, inicio, fim)) {
            return false;
        }
        return true;
    }
}