package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository NotificacaoRepository;

    public Notificacao insert(Notificacao Notificacao) {
        return NotificacaoRepository.save(Notificacao);
    }

    public List<Notificacao> listAll() {
        return NotificacaoRepository.findAll();
    }

    public Notificacao searchById(UUID id) {
        return NotificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacao não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        NotificacaoRepository.deleteById(id);
    }
}