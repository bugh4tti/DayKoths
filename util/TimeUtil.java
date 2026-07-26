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

    // 1 = Domingo ... 7 = Sabado (formato ES)
    public static String currentDayKey() {
        return nowArgentina().getDayOfWeek().name(); // MONDAY, TUESDAY, etc
    }
}
