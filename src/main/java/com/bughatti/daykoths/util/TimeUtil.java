package com.bughatti.daykoths.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {

    public static ZonedDateTime nowArgentina() {
        return ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
    }

    public static int currentHour() {
        return nowArgentina().getHour();
    }

    public static String currentDayKey() {
        return nowArgentina().getDayOfWeek().name();
    }

    public static String formatSeconds(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
