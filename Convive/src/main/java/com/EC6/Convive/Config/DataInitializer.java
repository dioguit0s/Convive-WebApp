package com.EC6.Convive.Config;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ModeradorRepository moderadorRepository;
    private final MoradorRepository moradorRepository;
    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final PasswordEncoder passwordEncoder;
    private final ComunicadoRepository comunicadoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final OcorrenciaRepository ocorrenciaRepository;

    private int protocoloSequencial = 1;

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedAreasComuns();
        seedOcorrencias();
        seedReservas();
        seedComunicados();
        seedNotificacoes();
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() > 0) return;

        Moderador moderador = new Moderador();
        moderador.setNome("Ana Silva");
        moderador.setEmail("moderador@convive.com");
        moderador.setSenhaHash(passwordEncoder.encode("moderador123"));
        moderador.setStatus("Ativo");
        moderadorRepository.save(moderador);

        Moderador diogo = new Moderador();
        diogo.setNome("Diogo Santos");
        diogo.setEmail("diogosantos152005@gmail.com");
        diogo.setSenhaHash(passwordEncoder.encode("diogos12"));
        diogo.setStatus("Ativo");
        moderadorRepository.save(diogo);

        criarMorador("Carlos Mendes", "carlos.mendes@email.com", 101, false);
        criarMorador("Maria Oliveira", "maria.oliveira@email.com", 205, false);
        criarMorador("João Pereira", "joao.pereira@email.com", 302, true);
        criarMorador("Fernanda Lima", "fernanda.lima@email.com", 408, false);
        criarMorador("Roberto Alves", "roberto.alves@email.com", 512, true);
        criarMorador("Patrícia Souza", "patricia.souza@email.com", 601, false);

        logCredenciais();
    }

    private Morador criarMorador(String nome, String email, int apartamento, boolean inadimplente) {
        Morador morador = new Morador();
        morador.setNome(nome);
        morador.setEmail(email);
        morador.setApartamento(apartamento);
        morador.setSenhaHash(passwordEncoder.encode("123"));
        morador.setStatus("Ativo");
        morador.setInadimplente(inadimplente);
        return moradorRepository.save(morador);
    }

    private void logCredenciais() {
        System.out.println("-----------------------------------------");
        System.out.println("USUÁRIOS DE TESTE CRIADOS");
        System.out.println("Moderador: moderador@convive.com / moderador123");
        System.out.println("Moradores: senha padrão 123 (ex.: carlos.mendes@email.com)");
        System.out.println("-----------------------------------------");
    }

    private void seedAreasComuns() {
        if (areaComumRepository.count() > 0) return;

        salvarArea("Piscina", 30, StatusArea.ATIVA);
        salvarArea("Salão de Festas", 80, StatusArea.ATIVA);
        salvarArea("Churrasqueira", 20, StatusArea.ATIVA);
        salvarArea("Academia", 25, StatusArea.ATIVA);
        salvarArea("Quadra Poliesportiva", 12, StatusArea.EM_MANUTENCAO);

        System.out.println("5 áreas comuns de teste criadas.");
    }

    private void salvarArea(String nome, int capacidade, StatusArea status) {
        AreaComum area = new AreaComum();
        area.setNome(nome);
        area.setCapacidade(capacidade);
        area.setStatusArea(status);
        areaComumRepository.save(area);
    }

    private void seedOcorrencias() {
        if (ocorrenciaRepository.count() > 0) return;

        List<Morador> moradores = moradorRepository.findAll();
        if (moradores.isEmpty()) return;

        LocalDateTime agora = LocalDateTime.now();

        salvarOcorrencia(moradores.get(0),
                "Vazamento no teto da garagem próximo à vaga 12.",
                Prioridade.ALTA, StatusOcorrencia.REGISTRADA, agora.minusHours(3));

        salvarOcorrencia(moradores.get(1),
                "Lâmpada queimada no corredor do 2º andar.",
                Prioridade.NAO_DEFINIDA, StatusOcorrencia.REGISTRADA, agora.minusDays(1));

        salvarOcorrencia(moradores.get(2),
                "Barulho excessivo após 23h no apartamento 302.",
                Prioridade.MEDIA, StatusOcorrencia.EM_ANALISE, agora.minusDays(2));

        salvarOcorrencia(moradores.get(3),
                "Portão da garagem com falha intermitente.",
                Prioridade.ALTA, StatusOcorrencia.EM_ANALISE, agora.minusDays(4));

        salvarOcorrencia(moradores.get(4),
                "Animal solto nas áreas comuns sem coleira.",
                Prioridade.BAIXA, StatusOcorrencia.REGISTRADA, agora.minusDays(6));

        salvarOcorrencia(moradores.get(0),
                "Uso indevido da churrasqueira fora do horário permitido.",
                Prioridade.MEDIA, StatusOcorrencia.RESOLVIDA, agora.minusDays(10));

        salvarOcorrencia(moradores.get(1),
                "Entrega de encomenda sem identificação na portaria.",
                Prioridade.BAIXA, StatusOcorrencia.RESOLVIDA, agora.minusDays(15));

        salvarOcorrencia(moradores.get(5),
                "Reclamação sobre limpeza da piscina.",
                Prioridade.NAO_DEFINIDA, StatusOcorrencia.REGISTRADA, agora.minusDays(20));

        salvarOcorrencia(moradores.get(2),
                "Solicitação de revisão de multa por ruído.",
                Prioridade.ALTA, StatusOcorrencia.REJEITADA, agora.minusDays(35));

        salvarOcorrencia(moradores.get(3),
                "Infiltração na parede da unidade 408.",
                Prioridade.MEDIA, StatusOcorrencia.RESOLVIDA, agora.minusDays(40));

        System.out.println("10 ocorrências de teste criadas.");
    }

    private void salvarOcorrencia(Morador morador, String descricao, Prioridade prioridade,
                                  StatusOcorrencia status, LocalDateTime dataRegistro) {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setUsuario(morador);
        ocorrencia.setDescricao(descricao);
        ocorrencia.setPrioridade(prioridade);
        ocorrencia.setStatus(status);
        ocorrencia.setDataRegistro(dataRegistro);
        ocorrencia.setProtocolo(gerarProtocolo());
        if (status == StatusOcorrencia.RESOLVIDA || status == StatusOcorrencia.EM_ANALISE) {
            ocorrencia.setRespostaModerador("Em acompanhamento pela administração do condomínio.");
        }
        ocorrenciaRepository.save(ocorrencia);
    }

    private String gerarProtocolo() {
        String ano = String.valueOf(Year.now().getValue());
        return ano + "-" + String.format("%04d", protocoloSequencial++);
    }

    private void seedReservas() {
        if (reservaRepository.count() > 0) return;

        List<Morador> moradores = moradorRepository.findAll();
        List<AreaComum> areas = areaComumRepository.findAll();
        if (moradores.isEmpty() || areas.isEmpty()) return;

        AreaComum piscina = buscarArea(areas, "Piscina");
        AreaComum salao = buscarArea(areas, "Salão de Festas");
        AreaComum churrasqueira = buscarArea(areas, "Churrasqueira");
        AreaComum academia = buscarArea(areas, "Academia");

        LocalDateTime agora = LocalDateTime.now();

        salvarReserva(moradores.get(0), piscina, agora.plusDays(2).withHour(14).withMinute(0),
                agora.plusDays(2).withHour(18).withMinute(0), StatusReserva.PENDENTE, 8);

        salvarReserva(moradores.get(1), salao, agora.plusDays(5).withHour(19).withMinute(0),
                agora.plusDays(5).withHour(23).withMinute(0), StatusReserva.PENDENTE, 40);

        salvarReserva(moradores.get(3), churrasqueira, agora.plusDays(1).withHour(12).withMinute(0),
                agora.plusDays(1).withHour(16).withMinute(0), StatusReserva.PENDENTE, 12);

        salvarReserva(moradores.get(4), academia, agora.plusDays(3).withHour(7).withMinute(0),
                agora.plusDays(3).withHour(9).withMinute(0), StatusReserva.PENDENTE, 4);

        salvarReserva(moradores.get(0), salao, agora.minusDays(5).withHour(18).withMinute(0),
                agora.minusDays(5).withHour(22).withMinute(0), StatusReserva.APROVADO, 35);

        salvarReserva(moradores.get(1), piscina, agora.minusDays(12).withHour(10).withMinute(0),
                agora.minusDays(12).withHour(14).withMinute(0), StatusReserva.APROVADO, 6);

        salvarReserva(moradores.get(2), churrasqueira, agora.minusDays(8).withHour(11).withMinute(0),
                agora.minusDays(8).withHour(15).withMinute(0), StatusReserva.APROVADO, 15);

        salvarReserva(moradores.get(5), academia, agora.minusDays(3).withHour(6).withMinute(0),
                agora.minusDays(3).withHour(8).withMinute(0), StatusReserva.APROVADO, 3);

        salvarReserva(moradores.get(3), piscina, agora.minusDays(20).withHour(15).withMinute(0),
                agora.minusDays(20).withHour(19).withMinute(0), StatusReserva.APROVADO, 10);

        Reserva reprovada = new Reserva();
        reprovada.setReservadoPor(moradores.get(2));
        reprovada.setAreaReservada(salao);
        reprovada.setInicio(agora.minusDays(2).withHour(20).withMinute(0));
        reprovada.setFim(agora.minusDays(2).withHour(23).withMinute(0));
        reprovada.setStatus(StatusReserva.REPROVADO);
        reprovada.setConvidadosEstimados(60);
        reprovada.setMotivoRejeicao("Capacidade do salão excedida para o horário solicitado.");
        reservaRepository.save(reprovada);

        System.out.println("10 reservas de teste criadas.");
    }

    private AreaComum buscarArea(List<AreaComum> areas, String nome) {
        return areas.stream()
                .filter(a -> nome.equals(a.getNome()))
                .findFirst()
                .orElse(areas.get(0));
    }

    private void salvarReserva(Morador morador, AreaComum area, LocalDateTime inicio, LocalDateTime fim,
                               StatusReserva status, int convidados) {
        Reserva reserva = new Reserva();
        reserva.setReservadoPor(morador);
        reserva.setAreaReservada(area);
        reserva.setInicio(inicio);
        reserva.setFim(fim);
        reserva.setStatus(status);
        reserva.setConvidadosEstimados(convidados);
        reservaRepository.save(reserva);
    }

    private void seedComunicados() {
        if (comunicadoRepository.count() > 0) return;

        Moderador autor = moderadorRepository.findAll().stream().findFirst().orElse(null);
        if (autor == null) return;

        LocalDateTime agora = LocalDateTime.now();
        List<Comunicado> comunicados = new ArrayList<>();

        comunicados.add(criarComunicado(autor, "Manutenção do Elevador Social",
                "O elevador social passará por manutenção preventiva na próxima terça-feira das 8h às 17h.",
                TipoComunicado.Obras, agora.minusHours(6)));

        comunicados.add(criarComunicado(autor, "Festa Junina do Condomínio",
                "Participe da nossa festa junina no sábado! Haverá comidas típicas, quadrilha e atividades para crianças.",
                TipoComunicado.Eventos, agora.minusDays(2)));

        comunicados.add(criarComunicado(autor, "Assembleia Geral Ordinária",
                "Convocamos todos os moradores para a assembleia no dia 28, às 19h, no salão de festas.",
                TipoComunicado.Reunião, agora.minusDays(5)));

        comunicados.add(criarComunicado(autor, "Novo horário da coleta seletiva",
                "A partir desta semana, a coleta seletiva ocorrerá às quartas e sextas-feiras às 7h.",
                TipoComunicado.Geral, agora.minusDays(8)));

        comunicados.add(criarComunicado(autor, "Pintura da fachada — Bloco B",
                "Informamos início das obras de pintura no Bloco B. Pedimos atenção à sinalização nas áreas comuns.",
                TipoComunicado.Obras, agora.minusDays(12)));

        comunicados.add(criarComunicado(autor, "Campanha de arrecadação — Brinquedoteca",
                "Estamos arrecadando brinquedos e livros infantis para a nova brinquedoteca do condomínio.",
                TipoComunicado.Eventos, agora.minusDays(18)));

        comunicados.add(criarComunicado(autor, "Atualização do regimento interno",
                "O novo regimento interno está disponível na portaria e no mural digital.",
                TipoComunicado.Geral, agora.minusDays(25)));

        comunicadoRepository.saveAll(comunicados);
        System.out.println(comunicados.size() + " comunicados de teste criados.");
    }

    private Comunicado criarComunicado(Moderador autor, String titulo, String conteudo,
                                       TipoComunicado tipo, LocalDateTime publicadoEm) {
        Comunicado comunicado = new Comunicado();
        comunicado.setTitulo(titulo);
        comunicado.setConteudo(conteudo);
        comunicado.setTipo(tipo);
        comunicado.setModerador(autor);
        comunicado.setPublicadoEm(publicadoEm);
        return comunicado;
    }

    private void seedNotificacoes() {
        if (notificacaoRepository.count() > 0) return;

        Moderador admin = moderadorRepository.findAll().stream().findFirst().orElse(null);
        List<Morador> moradores = moradorRepository.findAll();
        if (admin == null || moradores.isEmpty()) return;

        LocalDateTime agora = LocalDateTime.now();

        salvarNotificacao(admin, moradores.get(2), "Aviso de Som Alto",
                "Recebemos relatos de som alto após as 22h. Solicitamos atenção ao regimento interno.",
                GravidadeNotificacao.BAIXA, agora.minusDays(25), false);

        salvarNotificacao(admin, moradores.get(4), "Obstrução de Área Comum",
                "Objetos pessoais deixados no corredor. Favor liberar a passagem imediatamente.",
                GravidadeNotificacao.MEDIA, agora.minusDays(10), false);

        salvarNotificacao(admin, moradores.get(2), "Inadimplência — Taxa Condominial",
                "Identificamos pendência na taxa condominial referente ao mês anterior.",
                GravidadeNotificacao.ALTA, agora.minusDays(5), true);

        salvarNotificacao(admin, moradores.get(4), "Dano ao Elevador",
                "Constatamos avaria no elevador durante mudança. Multa aplicada conforme regimento.",
                GravidadeNotificacao.ALTA, agora.minusDays(2), true);

        salvarNotificacao(admin, moradores.get(0), "Estacionamento Irregular",
                "Veículo estacionado em vaga de visitante por mais de 24h.",
                GravidadeNotificacao.MEDIA, agora.minusDays(1), false);

        salvarNotificacao(admin, moradores.get(4), "Reincidência — Ruído Noturno",
                "Nova ocorrência de ruído após advertência anterior. Multa registrada.",
                GravidadeNotificacao.ALTA, agora.minusHours(8), true);

        System.out.println("6 notificações de teste criadas (3 com multa no período recente).");
    }

    private void salvarNotificacao(Moderador emitidoPor, Morador morador, String titulo, String descricao,
                                   GravidadeNotificacao gravidade, LocalDateTime dataEnvio, boolean gerouMulta) {
        Notificacao notificacao = new Notificacao();
        notificacao.setEmitidoPor(emitidoPor);
        notificacao.setMorador(morador);
        notificacao.setApartamento(morador.getApartamento());
        notificacao.setTitulo(titulo);
        notificacao.setDescricao(descricao);
        notificacao.setGravidade(gravidade);
        notificacao.setDataEnvio(dataEnvio);
        notificacao.setDataOcorrencia(dataEnvio.minusDays(1));
        notificacao.setGerouMulta(gerouMulta);
        notificacaoRepository.save(notificacao);
    }
}
