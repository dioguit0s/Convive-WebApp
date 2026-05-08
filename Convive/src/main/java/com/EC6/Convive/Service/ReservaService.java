package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}