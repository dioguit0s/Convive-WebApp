package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.ForgotPasswordForm;
import com.EC6.Convive.Model.Morador;
import com.EC6.Convive.Model.PasswordResetToken;
import com.EC6.Convive.Model.ResetPasswordForm;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    void getForgotPassword_showsForm() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/forgot-password"))
                .andExpect(model().attributeExists("forgotPasswordForm"))
                .andExpect(model().attribute("forgotPasswordForm", instanceOf(ForgotPasswordForm.class)));
    }

    @Test
    void postForgotPassword_valid_redirects() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .param("email", "morador@convive.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"));

        verify(passwordResetService).requestReset("morador@convive.com");
    }

    @Test
    void postForgotPassword_invalid_doesNotCallService() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .param("email", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/forgot-password"));

        verify(passwordResetService, never()).requestReset(anyString());
    }

    @Test
    void getResetPassword_missingToken_showsInvalid() throws Exception {
        mockMvc.perform(get("/reset-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/reset-password"))
                .andExpect(model().attribute("tokenInvalid", true));
    }

    @Test
    void getResetPassword_invalidToken_showsInvalid() throws Exception {
        when(passwordResetService.findValidToken("bad-token")).thenReturn(Optional.empty());

        mockMvc.perform(get("/reset-password").param("token", "bad-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/reset-password"))
                .andExpect(model().attribute("tokenInvalid", true));
    }

    @Test
    void getResetPassword_validToken_showsForm() throws Exception {
        when(passwordResetService.findValidToken("good-token")).thenReturn(Optional.of(validToken()));

        mockMvc.perform(get("/reset-password").param("token", "good-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/reset-password"))
                .andExpect(model().attributeExists("resetPasswordForm"));
    }

    @Test
    void postResetPassword_valid_redirectsToLogin() throws Exception {
        when(passwordResetService.findValidToken("good-token")).thenReturn(Optional.of(validToken()));

        mockMvc.perform(post("/reset-password")
                        .param("token", "good-token")
                        .param("password", "newpassword1")
                        .param("confirmPassword", "newpassword1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?reset=success"));

        verify(passwordResetService).resetPassword("good-token", "newpassword1", "newpassword1");
    }

    @Test
    void postResetPassword_shortPassword_staysOnPage() throws Exception {
        when(passwordResetService.findValidToken("good-token")).thenReturn(Optional.of(validToken()));

        mockMvc.perform(post("/reset-password")
                        .param("token", "good-token")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/reset-password"));

        verify(passwordResetService, never()).resetPassword(anyString(), anyString(), anyString());
    }

    private PasswordResetToken validToken() {
        Usuario user = new Morador();
        user.setId(UUID.randomUUID());
        user.setEmail("morador@convive.com");
        user.setStatus("Ativo");

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("good-token");
        token.setUsuario(user);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        return token;
    }
}
