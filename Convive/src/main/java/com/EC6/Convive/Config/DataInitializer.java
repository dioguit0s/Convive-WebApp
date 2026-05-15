package com.EC6.Convive.Config;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Repository.*;
import com.EC6.Convive.Service.AreaComumService;
import com.EC6.Convive.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final ComunicadoRepository comunicadoRepository;
    private final MoradorRepository moradorRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {

            Moderador moderador = new Moderador();
            moderador.setNome("Moderador");
            moderador.setEmail("moderador@convive.com");
            moderador.setSenhaHash(passwordEncoder.encode("moderador123"));
            moderador.setStatus("Ativo");

            Morador morador = new Morador();
            morador.setNome("Morador");
            morador.setApartamento(01);
            morador.setSenhaHash(passwordEncoder.encode("123"));
            morador.setStatus("Ativo");
            morador.setEmail("morador@convive.com");

            moderadorRepository.save(moderador);
            moradorRepository.save(morador);

            System.out.println("-----------------------------------------");
            System.out.println("USUÁRIO DE MODERADOR CRIADO:");
            System.out.println("Login: moderador@convive.com");
            System.out.println("Senha: moderador123");
            System.out.println("-----------------------------------------");
            System.out.println("-----------------------------------------");
            System.out.println("USUÁRIO DE MORADOR CRIADO:");
            System.out.println("Login: morador@convive.com");
            System.out.println("Senha: 123");
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
            Usuario user = usuarioService.getByEmail("moderador@convive.com");
            LocalDateTime inicialDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().plusHours(2);
            Reserva reserva = new Reserva();
            reserva.setReservadoPor(user);
            reserva.setInicio(inicialDate);
            reserva.setFim(endDate);
            reserva.setAreaReservada(areaComumService.searchByName("Piscina"));

            reservaRepository.save(reserva);
        }

        if (comunicadoRepository.count() == 0) {

            Moderador autor = moderadorRepository.findAll().get(0);

            Comunicado aviso1 = new Comunicado();
            aviso1.setTitulo("Manutenção da Piscina");
            aviso1.setConteudo("Informamos que a piscina estará em manutenção nesta sexta-feira durante todo o dia. Agradecemos a compreensão de todos.");
            aviso1.setPublicadoEm(LocalDateTime.now());
            aviso1.setModerador(autor);
            aviso1.setTipo(TipoComunicado.Obras);

            Comunicado aviso2 = new Comunicado();
            aviso2.setTitulo("Festa de Verão do Condomínio");
            aviso2.setConteudo("Não perca a nossa festa de verão no próximo sábado! Traga a família, haverá música, jogos e comida para todos.");
            aviso2.setPublicadoEm(LocalDateTime.now().minusDays(1));
            aviso2.setModerador(autor);
            aviso2.setTipo(TipoComunicado.Eventos);

            Comunicado aviso3 = new Comunicado();
            aviso3.setTitulo("Novas Regras de Estacionamento");
            aviso3.setConteudo("Por favor, reveja as novas regras de estacionamento para visitantes. É obrigatório identificar a matrícula do carro visitante na portaria.");
            aviso3.setPublicadoEm(LocalDateTime.now().minusDays(3));
            aviso3.setModerador(autor);
            aviso3.setTipo(TipoComunicado.Geral);

            comunicadoRepository.save(aviso1);
            comunicadoRepository.save(aviso2);
            comunicadoRepository.save(aviso3);

            System.out.println("3 Comunicados de teste criados com sucesso!");
        }
    }
}