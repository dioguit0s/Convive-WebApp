package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Repository.ComunicadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComunicadoService {

    private final ComunicadoRepository ComunicadoRepository;

    public Comunicado insert(Comunicado Comunicado) {
        return ComunicadoRepository.save(Comunicado);
    }

    public List<Comunicado> listAll() {
        return ComunicadoRepository.findAll();
    }

    public Comunicado searchById(UUID id) {
        return ComunicadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunicado não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        ComunicadoRepository.deleteById(id);
    }
}