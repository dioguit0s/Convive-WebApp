package com.EC6.Convive.Event;

import java.time.LocalDateTime;

public record ReservaRejeitadaEvent(
        String moradorEmail,
        LocalDateTime inicio,
        String motivoRejeicao
) {
}
