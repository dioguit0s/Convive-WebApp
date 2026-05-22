package com.EC6.Convive.Validator;

import com.EC6.Convive.Model.AreaComum;
import com.EC6.Convive.Model.StatusArea;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Service.AreaComumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MoradorHomeValidator {

    private static final ZoneId ZONA_APP = ZoneId.of("America/Sao_Paulo");
    private static final Set<String> TURNOS_VALIDOS = Set.of("manha", "tarde", "noite", "integral");

    private final AreaComumService areaComumService;

    public String validarNovaReserva(Usuario usuario, UUID areaId, LocalDate dataReserva, String turno, String convidadosEstimados) {

        if (usuario.isInadimplente()) {
            return "Não é possível realizar a reserva. Constam pendências financeiras em seu cadastro.";
        }

        if (turno == null || !TURNOS_VALIDOS.contains(turno.toLowerCase(Locale.ROOT))) {
            return "Turno inválido.";
        }

        LocalDate hoje = LocalDate.now(ZONA_APP);
        if (dataReserva.isBefore(hoje)) {
            return "A data não pode ser no passado.";
        }

        AreaComum area;
        try {
            area = areaComumService.searchById(areaId);
        } catch (RuntimeException ex) {
            return "Ambiente não encontrado.";
        }

        if (area.getStatusArea() != StatusArea.ATIVA) {
            return "Este ambiente não está disponível para reserva.";
        }

        if (convidadosEstimados != null && !convidadosEstimados.isBlank()) {
            try {
                int convidados = Integer.parseInt(convidadosEstimados.trim());
                if (convidados < 1) {
                    return "O número de convidados deve ser pelo menos 1.";
                }
                if (convidados > area.getCapacidade()) {
                    return "O número de convidados não pode exceder a capacidade do ambiente (" + area.getCapacidade() + ").";
                }
            } catch (NumberFormatException ex) {
                return "Número de convidados inválido.";
            }
        }
        return null;
    }
}