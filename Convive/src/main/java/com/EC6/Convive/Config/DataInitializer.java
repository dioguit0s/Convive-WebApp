package com.EC6.Convive.Config;

import com.EC6.Convive.Model.Moderador;
import com.EC6.Convive.Repository.ModeradorRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ModeradorRepository moderadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {

            Moderador admin = new Moderador();
            admin.setNome("Administrador Teste");
            admin.setEmail("admin@convive.com");
            admin.setSenhaHash(passwordEncoder.encode("admin123"));
            admin.setStatus("Ativo");

            moderadorRepository.save(admin);

            System.out.println("-----------------------------------------");
            System.out.println("USUÁRIO DE TESTE CRIADO:");
            System.out.println("Login: admin@convive.com");
            System.out.println("Senha: admin123");
            System.out.println("-----------------------------------------");
        }
    }
}