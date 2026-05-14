package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;

    public Ocorrencia insert(Ocorrencia ocorrencia) {
        ocorrencia.setDataRegistro(LocalDateTime.now());

        String anoAtual = String.valueOf(Year.now().getValue());
        String prefixo = anoAtual + "-";

        Optional<Ocorrencia> ultimaOcorrencia =  ocorrenciaRepository.findTopByProtocoloStartingWithOrderByProtocoloDesc(prefixo);
        int proximoNumero = 1;
        if(ultimaOcorrencia.isPresent() && ultimaOcorrencia.get().getProtocolo() != null) {
            String ultimoProtocolo = ultimaOcorrencia.get().getProtocolo();
            String[] partes = ultimoProtocolo.split("-");
            if(partes.length == 2) {
                proximoNumero = Integer.parseInt(partes[1]) + 1;
            }
        }

        String novoProtocolo = prefixo + String.format("%04d", proximoNumero);
        ocorrencia.setProtocolo(novoProtocolo);
        return ocorrenciaRepository.save(ocorrencia);
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

    public List<Ocorrencia> listByUser(UUID moradorId) {
        return ocorrenciaRepository.findByUsuarioId(moradorId);
    }
}