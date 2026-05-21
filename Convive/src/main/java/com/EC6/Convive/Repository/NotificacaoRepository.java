package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    List<Notificacao> getAllByMoradorId(UUID id);

    Page<Notificacao> findByMorador_IdOrderByDataEnvioDesc(UUID moradorId, Pageable pageable);

    long countByGerouMultaTrueAndDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);
}
