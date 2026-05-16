package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID> {
    List<Ocorrencia> findByUsuarioId(UUID moradorId);

    Optional<Ocorrencia> findTopByProtocoloStartingWithOrderByProtocoloDesc(String prefixoAno);
}
