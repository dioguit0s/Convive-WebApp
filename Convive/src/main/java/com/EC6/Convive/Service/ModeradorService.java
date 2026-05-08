package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Repository.ModeradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeradorService {

    private final ModeradorRepository moderadorRepository;

    public Moderador insert(Moderador Moderador) {
        return moderadorRepository.save(Moderador);
    }

    public List<Moderador> listAll() {
        return moderadorRepository.findAll();
    }

    public Moderador searchById(UUID id) {
        return moderadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderador não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        moderadorRepository.deleteById(id);
    }
}