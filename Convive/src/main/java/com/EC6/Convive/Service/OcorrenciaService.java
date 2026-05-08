package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;

    public Ocorrencia insert(Ocorrencia Ocorrencia) {
        return ocorrenciaRepository.save(Ocorrencia);
    }

    public List<Ocorrencia> listAll() {
        return ocorrenciaRepository.findAll();
    }

    public Ocorrencia searchById(UUID id) {
        return ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrencia não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        ocorrenciaRepository.deleteById(id);
    }
}