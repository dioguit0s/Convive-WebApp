package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.Morador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, UUID> {

    Page<Morador> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email, Pageable pageable);
}