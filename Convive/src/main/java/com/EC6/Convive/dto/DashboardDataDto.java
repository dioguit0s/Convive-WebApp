package com.EC6.Convive.dto;

import com.EC6.Convive.Model.Comunicado;
import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Reserva;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDataDto {
    private final long ocorrenciasPendentes;
    private final long ocorrenciasUrgentes;
    private final long moradoresInadimplentes;
    private final long totalMoradores;
    private final double taxaInadimplencia;
    private final long multasNoMes;
    private final List<ChartSliceDto> chartOcorrenciasStatus;
    private final List<ChartSliceDto> chartReservasPorArea;
    private final List<ChartSliceDto> chartComunicadosPorTipo;
    private final List<Ocorrencia> filaTriagem;
    private final List<Reserva> proximasReservas;
    private final List<Comunicado> comunicadosRecentes;
    private final String mesSelecionado;
    private final List<String> mesesDisponiveis;
}
