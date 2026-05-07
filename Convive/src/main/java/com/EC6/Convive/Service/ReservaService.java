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

    private final ReservaRepository ReservaRepository;

    public Reserva insert(Reserva Reserva) {
        return ReservaRepository.save(Reserva);
    }

    public List<Reserva> listAll() {
        return ReservaRepository.findAll();
    }

    public Reserva searchById(UUID id) {
        return ReservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        ReservaRepository.deleteById(id);
    }
}