package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Repository.MoradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoradorService {

    private final MoradorRepository moradorRepository;

    public Morador insert(Morador Morador) {
        return moradorRepository.save(Morador);
    }

    public List<Morador> listAll() {
        return moradorRepository.findAll();
    }

    public Morador searchById(UUID id) {
        return moradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        moradorRepository.deleteById(id);
    }
}