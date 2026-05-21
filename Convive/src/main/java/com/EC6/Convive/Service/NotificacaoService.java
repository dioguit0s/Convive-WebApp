package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Notificacao;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UserDetailsService userDetails;

    public Notificacao insert(Notificacao Notificacao) {
        return notificacaoRepository.save(Notificacao);
    }

    public List<Notificacao> listAll() {
        return notificacaoRepository.findAll();
    }

    public List<Notificacao> listAllByUserId(UUID user) {
        return notificacaoRepository.getAllByMoradorId(user);
    }

    public Page<Notificacao> findPaginatedByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataEnvio").descending());
        return notificacaoRepository.findByMorador_IdOrderByDataEnvioDesc(userId, pageable);
    }

    public Notificacao searchById(UUID id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacao não encontrada com o ID: " + id));
    }

    public void delete(UUID id) {
        notificacaoRepository.deleteById(id);
    }
}