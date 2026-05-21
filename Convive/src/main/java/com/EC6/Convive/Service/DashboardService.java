package com.EC6.Convive.Service;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Repository.*;
import com.EC6.Convive.dto.ChartSliceDto;
import com.EC6.Convive.dto.DashboardDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter MES_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MES_LABEL = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));

    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReservaRepository reservaRepository;
    private final ComunicadoRepository comunicadoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final MoradorRepository moradorRepository;

    public DashboardDataDto buildDashboard(String mesParam) {
        YearMonth mes = parseMes(mesParam);
        LocalDateTime inicio = mes.atDay(1).atStartOfDay();
        LocalDateTime fim = mes.atEndOfMonth().atTime(23, 59, 59);

        long pendentes = ocorrenciaRepository.countPendentes(StatusOcorrencia.REGISTRADA, Prioridade.NAO_DEFINIDA);
        long urgentes = ocorrenciaRepository.countByPrioridade(Prioridade.ALTA);

        long totalMoradores = moradorRepository.count();
        long inadimplentes = moradorRepository.countByIsInadimplenteTrue();
        double taxa = totalMoradores > 0 ? (inadimplentes * 100.0) / totalMoradores : 0.0;

        long multas = notificacaoRepository.countByGerouMultaTrueAndDataEnvioBetween(inicio, fim);

        List<Ocorrencia> fila = ocorrenciaRepository
                .findByStatusIn(List.of(StatusOcorrencia.REGISTRADA, StatusOcorrencia.EM_ANALISE))
                .stream()
                .sorted(Comparator
                        .comparingInt((Ocorrencia o) -> prioridadeOrdem(o.getPrioridade()))
                        .thenComparing(Ocorrencia::getDataRegistro))
                .limit(5)
                .toList();

        List<Reserva> reservas = reservaRepository.findProximasReservasPendentes(PageRequest.of(0, 5));
        List<Comunicado> comunicados = comunicadoRepository.findTop5ByPublicadoEmNotNullOrderByPublicadoEmDesc();

        return DashboardDataDto.builder()
                .ocorrenciasPendentes(pendentes)
                .ocorrenciasUrgentes(urgentes)
                .moradoresInadimplentes(inadimplentes)
                .totalMoradores(totalMoradores)
                .taxaInadimplencia(taxa)
                .multasNoMes(multas)
                .chartOcorrenciasStatus(mapOcorrenciasStatus(inicio, fim))
                .chartReservasPorArea(mapReservasPorArea(inicio, fim))
                .chartComunicadosPorTipo(mapComunicadosPorTipo(inicio, fim))
                .filaTriagem(fila)
                .proximasReservas(reservas)
                .comunicadosRecentes(comunicados)
                .mesSelecionado(mes.format(MES_FORMAT))
                .mesesDisponiveis(ultimosMeses(6))
                .build();
    }

    private YearMonth parseMes(String mesParam) {
        if (mesParam != null && !mesParam.isBlank()) {
            try {
                return YearMonth.parse(mesParam, MES_FORMAT);
            } catch (Exception ignored) {
            }
        }
        return YearMonth.now();
    }

    private List<String> ultimosMeses(int quantidade) {
        YearMonth atual = YearMonth.now();
        return IntStream.range(0, quantidade)
                .mapToObj(i -> atual.minusMonths(i).format(MES_FORMAT))
                .toList();
    }

    private List<ChartSliceDto> mapOcorrenciasStatus(LocalDateTime inicio, LocalDateTime fim) {
        List<Object[]> rows = ocorrenciaRepository.countGroupedByStatusBetween(inicio, fim);
        List<ChartSliceDto> slices = new ArrayList<>();
        for (Object[] row : rows) {
            StatusOcorrencia status = (StatusOcorrencia) row[0];
            Long count = (Long) row[1];
            slices.add(new ChartSliceDto(formatStatus(status), count));
        }
        if (slices.isEmpty()) {
            Arrays.stream(StatusOcorrencia.values())
                    .forEach(s -> slices.add(new ChartSliceDto(formatStatus(s), 0)));
        }
        return slices;
    }

    private List<ChartSliceDto> mapReservasPorArea(LocalDateTime inicio, LocalDateTime fim) {
        List<Object[]> rows = reservaRepository.countAprovadasPorAreaNoMes(inicio, fim);
        List<ChartSliceDto> slices = new ArrayList<>();
        for (Object[] row : rows) {
            String nome = (String) row[0];
            Long count = (Long) row[1];
            slices.add(new ChartSliceDto(nome != null ? nome : "Sem área", count));
        }
        return slices;
    }

    private List<ChartSliceDto> mapComunicadosPorTipo(LocalDateTime inicio, LocalDateTime fim) {
        List<Object[]> rows = comunicadoRepository.countGroupedByTipoBetween(inicio, fim);
        List<ChartSliceDto> slices = new ArrayList<>();
        for (Object[] row : rows) {
            TipoComunicado tipo = (TipoComunicado) row[0];
            Long count = (Long) row[1];
            slices.add(new ChartSliceDto(tipo != null ? tipo.name() : "Outros", count));
        }
        return slices;
    }

    private String formatStatus(StatusOcorrencia status) {
        return switch (status) {
            case REGISTRADA -> "Registrada";
            case EM_ANALISE -> "Em Análise";
            case RESOLVIDA -> "Resolvida";
            case REJEITADA -> "Rejeitada";
        };
    }

    public String formatMesLabel(String mesIso) {
        return YearMonth.parse(mesIso, MES_FORMAT).format(MES_LABEL);
    }

    private int prioridadeOrdem(Prioridade prioridade) {
        if (prioridade == null) return 4;
        return switch (prioridade) {
            case ALTA -> 0;
            case MEDIA -> 1;
            case BAIXA -> 2;
            case NAO_DEFINIDA -> 3;
        };
    }
}
