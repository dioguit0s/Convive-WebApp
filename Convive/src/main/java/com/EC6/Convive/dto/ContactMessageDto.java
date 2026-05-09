package com.EC6.Convive.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageDto {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres.")
    private String fullName;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 320, message = "E-mail deve ter no máximo 320 caracteres.")
    private String email;

    @NotBlank(message = "Informe o assunto.")
    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres.")
    private String subject;

    @NotBlank(message = "Escreva sua mensagem.")
    @Size(max = 5000, message = "Mensagem deve ter no máximo 5000 caracteres.")
    private String message;
}
