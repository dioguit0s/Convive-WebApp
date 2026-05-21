package com.EC6.Convive.Config;

import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.UsuarioService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expõe o usuário autenticado no model das views, sempre recarregado do banco
 * (inclui {@code fotoPerfil} atualizada após upload, que não reflete na sessão).
 */
@ControllerAdvice
public class AuthenticatedUserModelAdvice {

    private final ObjectProvider<UsuarioService> usuarioServiceProvider;

    public AuthenticatedUserModelAdvice(ObjectProvider<UsuarioService> usuarioServiceProvider) {
        this.usuarioServiceProvider = usuarioServiceProvider;
    }

    @ModelAttribute("usuario")
    public Usuario usuarioAutenticado(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        UsuarioService usuarioService = usuarioServiceProvider.getIfAvailable();
        if (usuarioService == null) {
            return null;
        }
        return usuarioService.getByEmail(userDetails.getUsername());
    }
}
