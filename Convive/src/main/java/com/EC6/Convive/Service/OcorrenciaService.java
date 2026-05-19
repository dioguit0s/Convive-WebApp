package com.EC6.Convive.Service;

import com.EC6.Convive.Model.ContactMessageModel;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final ModeradorService moderadorService;
    private final ContactMailService contactMailService;

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

        //envia email para moderacao
        ContactMessageModel model = new ContactMessageModel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM ");
        List<Moderador> allMods = moderadorService.getAllActiveMods();
        model.setSubject("Nova ocorrencia registrada e necessitando triagem");
        model.setMessage("""
                    Um usuario cadastrou uma nova ocorrencia as %s
                    Detalhes:
                    Registrado por: %s
                    Descrição: %s
                    """.formatted(ocorrencia.getDataRegistro().format(formatter), ocorrencia.getUsuario().getNome(), ocorrencia.getDescricao())
        );
        for(Moderador mod : allMods) {
            model.setEmail(mod.getEmail());
            model.setFullName(mod.getNome());
            //contactMailService.sendToOutside(model);
        }

        return ocorrenciaRepository.save(ocorrencia);
    }

    public Ocorrencia update(Ocorrencia ocorrencia) {
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