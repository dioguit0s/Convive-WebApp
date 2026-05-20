package com.EC6.Convive.Service;

import com.EC6.Convive.Model.PasswordResetToken;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.PasswordResetTokenRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final String ACTIVE_STATUS = "Ativo";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetMailService passwordResetMailService;

    @Value("${app.password-reset.expiration-minutes}")
    private int expirationMinutes;

    @Transactional
    public void requestReset(String email) {
        usuarioRepository.findByEmail(email.trim())
                .filter(this::isActiveUser)
                .ifPresentOrElse(
                        this::createAndSendToken,
                        () -> log.info("Reset de senha: solicitação ignorada (e-mail inexistente ou inativo)")
                );
    }

    public Optional<PasswordResetToken> findValidToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByTokenAndUsedAtIsNull(token.trim())
                .filter(PasswordResetToken::isValid);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        PasswordResetToken resetToken = findValidToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Link de redefinição inválido ou expirado."));

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);

        log.info("Reset de senha: senha atualizada | usuarioId={}", usuario.getId());
    }

    private boolean isActiveUser(Usuario usuario) {
        return ACTIVE_STATUS.equals(usuario.getStatus());
    }

    private void createAndSendToken(Usuario usuario) {
        tokenRepository.deleteByUsuario_Id(usuario.getId());

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(generateToken());
        resetToken.setUsuario(usuario);
        resetToken.setCreatedAt(Instant.now());
        resetToken.setExpiresAt(Instant.now().plusSeconds(expirationMinutes * 60L));
        tokenRepository.save(resetToken);

        String recipientEmail = usuario.getEmail();
        String tokenValue = resetToken.getToken();
        scheduleResetEmail(recipientEmail, tokenValue);
        log.info("Reset de senha: token criado | usuarioId={}", usuario.getId());
    }

    private void scheduleResetEmail(String recipientEmail, String tokenValue) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    passwordResetMailService.sendResetLink(recipientEmail, tokenValue);
                }
            });
        } else {
            passwordResetMailService.sendResetLink(recipientEmail, tokenValue);
        }
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
