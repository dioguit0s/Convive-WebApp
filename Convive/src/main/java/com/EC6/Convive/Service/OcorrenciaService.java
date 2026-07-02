package com.EC6.Convive.Service;

import com.EC6.Convive.Event.OcorrenciaCriadaEvent;
import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
import com.EC6.Convive.Model.StatusOcorrencia;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.OcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ApplicationEventPublisher eventPublisher;

    public void aplicarPrioridadePadrao(Ocorrencia ocorrencia) {
        if (ocorrencia.getCategoria() == null) {
            ocorrencia.setPrioridade(Prioridade.NAO_DEFINIDA);
            return;
        }
        ocorrencia.setPrioridade(ocorrencia.getCategoria().getPrioridadePadrao());
    }

    public Ocorrencia insert(Ocorrencia ocorrencia) {
        aplicarPrioridadePadrao(ocorrencia);
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

        Ocorrencia saved = ocorrenciaRepository.save(ocorrencia);

        eventPublisher.publishEvent(new OcorrenciaCriadaEvent(
                saved.getDataRegistro(),
                saved.getUsuario().getNome(),
                saved.getTitulo(),
                saved.getCategoria().getRotulo(),
                saved.getDescricao()
        ));

        return saved;
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

    public boolean deleteForUser(UUID ocorrenciaId, Usuario usuario) {
        Optional<Ocorrencia> ocorrencia;
        if (usuario instanceof Moderador) {
            ocorrencia = ocorrenciaRepository.findById(ocorrenciaId);
        } else {
            ocorrencia = ocorrenciaRepository.findByIdAndUsuario_Id(ocorrenciaId, usuario.getId());
        }
        if (ocorrencia.isEmpty()) {
            return false;
        }
        ocorrenciaRepository.delete(ocorrencia.get());
        return true;
    }

    public List<Ocorrencia> listByUser(UUID moradorId) {
        return ocorrenciaRepository.findByUsuarioId(moradorId);
    }

    public Page<Ocorrencia> findTriagemPaginated(int page, int size, String busca, String statusParam,
                                                 String prioridadeParam, String ordem) {
        String buscaNorm = normalizeBusca(busca);
        StatusOcorrencia status = parseStatusFilter(statusParam);
        Prioridade prioridade = parsePrioridadeFilter(prioridadeParam);
        Sort sort = "ASC".equalsIgnoreCase(ordem)
                ? Sort.by("dataRegistro").ascending()
                : Sort.by("dataRegistro").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ocorrenciaRepository.findTriagem(buscaNorm, status, prioridade, pageable);
    }

    public Page<Ocorrencia> findPaginatedByUser(UUID moradorId, int page, int size, String busca,
                                                String statusParam, String prioridadeParam, String ordem) {
        String buscaNorm = normalizeBusca(busca);
        StatusOcorrencia status = parseStatusFilter(statusParam);
        Prioridade prioridade = parsePrioridadeFilter(prioridadeParam);
        Pageable pageable = PageRequest.of(page, size, resolveSort(ordem));
        return ocorrenciaRepository.findByUsuarioTriagem(moradorId, buscaNorm, status, prioridade, pageable);
    }

    private static Sort resolveSort(String ordem) {
        return "ASC".equalsIgnoreCase(ordem)
                ? Sort.by("dataRegistro").ascending()
                : Sort.by("dataRegistro").descending();
    }

    private static String normalizeBusca(String busca) {
        if (busca == null || busca.isBlank()) {
            return null;
        }
        return busca.trim();
    }

    private static StatusOcorrencia parseStatusFilter(String statusParam) {
        if (statusParam == null || statusParam.isBlank() || "ALL".equalsIgnoreCase(statusParam)) {
            return null;
        }
        return StatusOcorrencia.valueOf(statusParam);
    }

    private static Prioridade parsePrioridadeFilter(String prioridadeParam) {
        if (prioridadeParam == null || prioridadeParam.isBlank() || "ALL".equalsIgnoreCase(prioridadeParam)) {
            return null;
        }
        return Prioridade.valueOf(prioridadeParam);
    }

}
