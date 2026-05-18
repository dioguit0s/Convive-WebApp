package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Moderador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModeradorRepository extends JpaRepository<Moderador, UUID> {

    List<Moderador> findAllByStatus(String status);
}
