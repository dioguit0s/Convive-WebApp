package com.EC6.Convive.Repository;

import com.EC6.Convive.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenAndUsedAtIsNull(String token);

    void deleteByUsuario_Id(UUID usuarioId);
}
