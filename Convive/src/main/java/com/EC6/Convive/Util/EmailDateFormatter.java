package com.EC6.Convive.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EmailDateFormatter {

    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private EmailDateFormatter() {
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "—";
        }
        return dateTime.format(DISPLAY);
    }
}
