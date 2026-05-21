package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Reserva;
import com.EC6.Convive.Model.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    List<Reserva> findByReservadoPorId(UUID usuarioId);

    Page<Reserva> findByReservadoPorIdOrderByInicioDesc(UUID usuarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"reservadoPor", "areaReservada"})
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.status = :status
            AND (:busca IS NULL OR LOWER(r.reservadoPor.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(COALESCE(r.reservadoPor.email, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
                OR LOWER(r.areaReservada.nome) LIKE LOWER(CONCAT('%', :busca, '%')))
            """)
    Page<Reserva> findTriagem(@Param("status") StatusReserva status,
                              @Param("busca") String busca,
                              Pageable pageable);

    Optional<Reserva> findByIdAndReservadoPor_Id(UUID id, UUID reservadoPorId);

    boolean existsByAreaReservadaIdAndStatusInAndInicioLessThanAndFimGreaterThan(
            UUID areaId,
            Collection<StatusReserva> statuses,
            LocalDateTime novoFim,
            LocalDateTime novoInicio
    );

    boolean existsByReservadoPorIdAndAreaReservadaIdAndStatusInAndInicioLessThanAndFimGreaterThan(
            UUID reservadoPorId,
            UUID areaId,
            Collection<StatusReserva> statuses,
            LocalDateTime novoFim,
            LocalDateTime novoInicio
    );

    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.areaReservada.id = :areaId")
    void deleteAllReservasWithThisArea(@Param("areaId") UUID areaId);

    @Query("""
            SELECT r.areaReservada.nome, COUNT(r) FROM Reserva r
            WHERE r.status = com.EC6.Convive.Model.StatusReserva.APROVADO
            AND r.inicio BETWEEN :inicio AND :fim
            GROUP BY r.areaReservada.nome
            ORDER BY COUNT(r) DESC
            """)
    List<Object[]> countAprovadasPorAreaNoMes(@Param("inicio") LocalDateTime inicio,
                                              @Param("fim") LocalDateTime fim);

    @Query("""
            SELECT r FROM Reserva r
            WHERE r.status = com.EC6.Convive.Model.StatusReserva.PENDENTE
            ORDER BY r.inicio ASC
            """)
    List<Reserva> findProximasReservasPendentes(org.springframework.data.domain.Pageable pageable);
}
