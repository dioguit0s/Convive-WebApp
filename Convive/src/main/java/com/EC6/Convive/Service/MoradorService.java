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

    private final MoradorRepository MoradorRepository;

    public Morador insert(Morador Morador) {
        return MoradorRepository.save(Morador);
    }

    public List<Morador> listAll() {
        return MoradorRepository.findAll();
    }

    public Morador searchById(UUID id) {
        return MoradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        MoradorRepository.deleteById(id);
    }
}