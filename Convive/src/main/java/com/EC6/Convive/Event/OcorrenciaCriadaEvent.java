package com.EC6.Convive.Event;

import java.time.LocalDateTime;

public record OcorrenciaCriadaEvent(
        LocalDateTime dataRegistro,
        String nomeMorador,
        String titulo,
        String categoria,
        String descricao
) {
}
