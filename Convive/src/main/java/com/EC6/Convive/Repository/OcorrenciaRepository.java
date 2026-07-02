package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
import com.EC6.Convive.Model.StatusOcorrencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    Optional<Ocorrencia> findByIdAndUsuario_Id(UUID id, UUID usuarioId);

    Page<Ocorrencia> findByUsuarioIdOrderByDataRegistroDesc(UUID moradorId, Pageable pageable);

    @Query("""
            SELECT o FROM Ocorrencia o
            WHERE o.usuario.id = :moradorId
            AND (:busca IS NULL OR LOWER(o.protocolo) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(o.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
            AND (:status IS NULL OR o.status = :status)
            AND (:prioridade IS NULL OR o.prioridade = :prioridade)
            """)
    Page<Ocorrencia> findByUsuarioTriagem(@Param("moradorId") UUID moradorId,
                                          @Param("busca") String busca,
                                          @Param("status") StatusOcorrencia status,
                                          @Param("prioridade") Prioridade prioridade,
                                          Pageable pageable);

    @EntityGraph(attributePaths = "usuario")
    @Query("""
            SELECT o FROM Ocorrencia o
            WHERE (:busca IS NULL OR LOWER(o.protocolo) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(o.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
            AND (:status IS NULL OR o.status = :status)
            AND (:prioridade IS NULL OR o.prioridade = :prioridade)
            """)
    Page<Ocorrencia> findTriagem(@Param("busca") String busca,
                                 @Param("status") StatusOcorrencia status,
                                 @Param("prioridade") Prioridade prioridade,
                                 Pageable pageable);

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
