package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, UUID> {
}
