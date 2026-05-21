package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Comunicado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, UUID> {
    List<Comunicado> getAllByPublicadoEmNotNullOrderByPublicadoEmDesc();

    Page<Comunicado> findAllByOrderByPublicadoEmDesc(Pageable pageable);

    @Query("SELECT c.tipo, COUNT(c) FROM Comunicado c WHERE c.publicadoEm BETWEEN :inicio AND :fim GROUP BY c.tipo")
    List<Object[]> countGroupedByTipoBetween(@Param("inicio") LocalDateTime inicio,
                                             @Param("fim") LocalDateTime fim);

    List<Comunicado> findTop5ByPublicadoEmNotNullOrderByPublicadoEmDesc();
}
