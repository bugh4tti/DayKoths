package com.bughatti.daykoths.util;

import com.bughatti.daykoths.model.Koth;

import java.time.ZonedDateTime;
import java.util.Map;

public class ScheduleUtil {

    private static final String[] SPANISH_DAYS = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    public static String getNextStart(Koth koth) {
        ZonedDateTime now = TimeUtil.nowArgentina();

        for (int dayOffset = 0; dayOffset < 8; dayOffset++) {
            ZonedDateTime day = now.plusDays(dayOffset);
            String dayKey = day.getDayOfWeek().name();
            int startHour = (dayOffset == 0) ? day.getHour() + 1 : 0;

            for (int hour = startHour; hour < 24; hour++) {
                boolean active = isActive(koth, "ALLDAYS", hour) || isActive(koth, dayKey, hour);
                if (active) {
                    String dayLabel = dayOffset == 0 ? "Hoy" : (dayOffset == 1 ? "Mañana" : SPANISH_DAYS[day.getDayOfWeek().getValue() - 1]);
                    return dayLabel + " " + String.format("%02d:00", hour);
                }
            }
        }
        return "No programado";
    }

    public static boolean isActiveNow(Koth koth) {
        ZonedDateTime now = TimeUtil.nowArgentina();
        int hour = now.getHour();
        String dayKey = now.getDayOfWeek().name();
        return isActive(koth, "ALLDAYS", hour) || isActive(koth, dayKey, hour);
    }

    public static String currentKey() {
        ZonedDateTime now = TimeUtil.nowArgentina();
        return now.getDayOfWeek().name() + "-" + now.getHour();
    }

    private static boolean isActive(Koth koth, String dayKey, int hour) {
        Map<Integer, Boolean> hours = koth.getSchedules().get(dayKey);
        return hours != null && Boolean.TRUE.equals(hours.get(hour));
    }
}
