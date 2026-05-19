package com.EC6.Convive.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordForm {

    @NotBlank(message = "Informe o token de redefinição.")
    private String token;

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;

    @NotBlank(message = "Confirme a nova senha.")
    private String confirmPassword;
}
