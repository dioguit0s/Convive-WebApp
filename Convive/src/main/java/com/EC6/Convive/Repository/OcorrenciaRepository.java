package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
import com.EC6.Convive.Model.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID> {
    List<Ocorrencia> findByUsuarioId(UUID moradorId);

    Optional<Ocorrencia> findTopByProtocoloStartingWithOrderByProtocoloDesc(String prefixoAno);

    long countByPrioridade(Prioridade prioridade);

    @Query("SELECT COUNT(o) FROM Ocorrencia o WHERE o.status = :statusRegistrada OR o.prioridade = :prioridadeNaoDefinida")
    long countPendentes(@Param("statusRegistrada") StatusOcorrencia statusRegistrada,
                        @Param("prioridadeNaoDefinida") Prioridade prioridadeNaoDefinida);

    @Query("SELECT o.status, COUNT(o) FROM Ocorrencia o WHERE o.dataRegistro BETWEEN :inicio AND :fim GROUP BY o.status")
    List<Object[]> countGroupedByStatusBetween(@Param("inicio") LocalDateTime inicio,
                                               @Param("fim") LocalDateTime fim);

    List<Ocorrencia> findByStatusIn(List<StatusOcorrencia> statuses);
}
