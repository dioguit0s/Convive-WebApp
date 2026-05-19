package com.EC6.Convive.Event;

import java.time.LocalDateTime;

public record ReservaPendenteCriadaEvent(
        String areaNome,
        String moradorNome,
        LocalDateTime inicio,
        String observacoes
) {
}
