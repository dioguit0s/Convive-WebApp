package com.EC6.Convive.Config;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Repository.AreaComumRepository;
import com.EC6.Convive.Repository.ModeradorRepository;
import com.EC6.Convive.Repository.ReservaRepository;
import com.EC6.Convive.Repository.UsuarioRepository;
import com.EC6.Convive.Service.AreaComumService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ModeradorRepository moderadorRepository;
    private final UsuarioService usuarioService;
    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final PasswordEncoder passwordEncoder;
    private final AreaComumService areaComumService;

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

        if(areaComumRepository.count() == 0) {
            AreaComum newArea = new AreaComum();
            newArea.setCapacidade(5);
            newArea.setNome("Piscina");
            newArea.setStatusArea(StatusArea.ATIVA);

            areaComumRepository.save(newArea);
        }

        if (reservaRepository.count() == 0) {
            Usuario user = usuarioService.getByEmail("admin@convive.com");
            LocalDateTime inicialDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now();
            Reserva reserva = new Reserva();
            reserva.setReservadoPor(user);
            reserva.setInicio(inicialDate);
            reserva.setFim(endDate);
            reserva.setAreaReservada(areaComumService.searchByName("Piscina"));

            reservaRepository.save(reserva);
        }
    }
}