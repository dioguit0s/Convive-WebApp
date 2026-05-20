package com.EC6.Convive.Service;

import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.PasswordResetToken;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Repository.PasswordResetTokenRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordResetMailService passwordResetMailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Usuario activeUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "expirationMinutes", 60);
        activeUser = new Morador();
        activeUser.setId(UUID.randomUUID());
        activeUser.setEmail("morador@convive.com");
        activeUser.setStatus("Ativo");
        activeUser.setSenhaHash("old-hash");
    }

    @Test
    void requestReset_activeUser_createsTokenAndSendsEmail() {
        when(usuarioRepository.findByEmail("morador@convive.com")).thenReturn(Optional.of(activeUser));

        passwordResetService.requestReset("morador@convive.com");

        verify(tokenRepository).deleteByUsuario_Id(activeUser.getId());
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(captor.capture());
        PasswordResetToken saved = captor.getValue();
        assertNotNull(saved.getToken());
        assertEquals(activeUser, saved.getUsuario());
        assertTrue(saved.getExpiresAt().isAfter(Instant.now()));
        verify(passwordResetMailService).sendResetLink("morador@convive.com", saved.getToken());
    }

    @Test
    void requestReset_unknownEmail_doesNotCreateToken() {
        when(usuarioRepository.findByEmail("unknown@convive.com")).thenReturn(Optional.empty());

        passwordResetService.requestReset("unknown@convive.com");

        verify(tokenRepository, never()).save(any());
        verify(passwordResetMailService, never()).sendResetLink(any(), any());
    }

    @Test
    void requestReset_inactiveUser_doesNotCreateToken() {
        activeUser.setStatus("Inativo");
        when(usuarioRepository.findByEmail("morador@convive.com")).thenReturn(Optional.of(activeUser));

        passwordResetService.requestReset("morador@convive.com");

        verify(tokenRepository, never()).save(any());
        verify(passwordResetMailService, never()).sendResetLink(any(), any());
    }

    @Test
    void findValidToken_returnsTokenWhenValid() {
        PasswordResetToken token = validToken("abc-token");
        when(tokenRepository.findByTokenAndUsedAtIsNull("abc-token")).thenReturn(Optional.of(token));

        Optional<PasswordResetToken> result = passwordResetService.findValidToken("abc-token");

        assertTrue(result.isPresent());
    }

    @Test
    void findValidToken_emptyWhenExpired() {
        PasswordResetToken token = validToken("expired-token");
        token.setExpiresAt(Instant.now().minusSeconds(60));
        when(tokenRepository.findByTokenAndUsedAtIsNull("expired-token")).thenReturn(Optional.of(token));

        Optional<PasswordResetToken> result = passwordResetService.findValidToken("expired-token");

        assertTrue(result.isEmpty());
    }

    @Test
    void resetPassword_success_updatesHashAndMarksTokenUsed() {
        PasswordResetToken token = validToken("reset-token");
        when(tokenRepository.findByTokenAndUsedAtIsNull("reset-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpassword1")).thenReturn("new-hash");

        passwordResetService.resetPassword("reset-token", "newpassword1", "newpassword1");

        assertEquals("new-hash", activeUser.getSenhaHash());
        verify(usuarioRepository).save(activeUser);
        assertNotNull(token.getUsedAt());
        verify(tokenRepository).save(token);
    }

    @Test
    void resetPassword_mismatch_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("reset-token", "newpassword1", "different"));

        verify(tokenRepository, never()).findByTokenAndUsedAtIsNull(any());
    }

    @Test
    void resetPassword_invalidToken_throws() {
        when(tokenRepository.findByTokenAndUsedAtIsNull("bad")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("bad", "newpassword1", "newpassword1"));
    }

    private PasswordResetToken validToken(String value) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(value);
        token.setUsuario(activeUser);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        return token;
    }
}
