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

    private final OcorrenciaRepository OcorrenciaRepository;

    public Ocorrencia insert(Ocorrencia Ocorrencia) {
        return OcorrenciaRepository.save(Ocorrencia);
    }

    public List<Ocorrencia> listAll() {
        return OcorrenciaRepository.findAll();
    }

    public Ocorrencia searchById(UUID id) {
        return OcorrenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrencia não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        OcorrenciaRepository.deleteById(id);
    }
}