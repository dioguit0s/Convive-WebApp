package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


@Repository
public interface AdminstradorRepository extends JpaRepository<Administrador, UUID> {
}
