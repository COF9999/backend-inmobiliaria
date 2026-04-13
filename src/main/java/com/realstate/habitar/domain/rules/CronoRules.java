package com.realstate.habitar.domain.rules;

import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class CronoRules {

    private static final ZoneId zone = ZoneId.of("GMT-5");

    public static LocalDateTime currentLocalDatime(){
        return ZonedDateTime.now(zone).toLocalDateTime();
    }

    public static LocalDateTime stringToDateWithZone(String strDate){
       return OffsetDateTime
                .parse(strDate)
                .atZoneSameInstant(zone)
                .toLocalDateTime();
    }

    private static LocalDateTime mileSecondsEndToday(){
        return LocalDateTime.now(zone)
                .with(LocalTime.of(23, 59, 59, 0));
    }

    private static long milesecondsByDayAgo(Long days) {
        return mileSecondsEndToday() // Forzamos 23:59:59.000
                .minusDays(days)
                .atZone(zone)
                .toInstant()
                .toEpochMilli();
    }

    public static LiquidationTimeRecord getMileSecondsTime(LiquidationTimeRecord liquidationTimeRecord){
        LocalDateTime dateOne;
        LocalDateTime dateSecond;
        long mileSecondsStart;
        long mileSecondsEnd;

        if (liquidationTimeRecord.onlyDay()!=null){
            System.out.println("ENTROROROORO");

            int onlyDay = liquidationTimeRecord.onlyDay();
            if (onlyDay<=0 || onlyDay>31){
                throw new IllegalArgumentException("El parametro para los días no esta en un rango valido");
            }
            mileSecondsStart = milesecondsByDayAgo((long) onlyDay);
            mileSecondsEnd = mileSecondsEndToday()
                    .toInstant(ZoneOffset.of("-05:00"))
                    .toEpochMilli();
            return new LiquidationTimeRecord.BuilderLiquidationTime()
                    .setOnlyDay(onlyDay)
                    .setMilesecondsStartDay(mileSecondsStart)
                    .setMilesecondsEndDay(mileSecondsEnd)
                    .build();

        }else {
            mileSecondsStart = mileSecondsOfDate(liquidationTimeRecord.dateOne());
            mileSecondsEnd = mileSecondsOfDate(liquidationTimeRecord.dateSecond());
            return new LiquidationTimeRecord.BuilderLiquidationTime()
                    .setMilesecondsStartDay(mileSecondsStart)
                    .setMilesecondsEndDay(mileSecondsEnd)
                    .build();
        }
    }

    private static long mileSecondsOfDate(LocalDateTime ldtDate){
        return ldtDate
                .toLocalDate()
                .atStartOfDay()
                .atZone(zone)
                .toInstant()
                .toEpochMilli();
    }

    /*---*/

    public static LocalDateTime dateEndCurrentMonth(LocalDateTime localDateTime) {
        // Obtenemos el último día posible del mes actual (28, 29, 30 o 31)
        int lastDayMonth = localDateTime.getMonth().length(localDateTime.toLocalDate().isLeapYear());

        return localDateTime.withDayOfMonth(lastDayMonth);
    }

    public static LocalDateTime adjustTo30(LocalDateTime currentDate){
        // Si el mes tiene menos de 30 días (Febrero), vamos al 1 de Marzo
        return (currentDate.getMonthValue() == 2)
                ? currentDate.plusMonths(1).withDayOfMonth(1)
                : currentDate.withDayOfMonth(30);
    }

    public static LocalDateTime dateStartCurrentMonth(LocalDateTime localDateTime){
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static long differenceBeetweenDates(LocalDateTime baseDate,LocalDateTime endCurrentDate){
        int dayStart = baseDate.getDayOfMonth();
        int dayEnd = endCurrentDate.getDayOfMonth();

        // Normalización exclusiva para FEBRERO
        if (baseDate.getMonthValue() == 2 && isLastDayOfMonth(baseDate)) dayStart = 30;
        if (endCurrentDate.getMonthValue() == 2 && isLastDayOfMonth(endCurrentDate)) dayEnd = 30;

        // Normalización para meses de 31 (ya sea inicio o fin)
        if (dayStart == 31) dayStart = 30;
        if (dayEnd == 31) dayEnd = 30;

        return (dayEnd - dayStart) + 1;
    }

    private static boolean isLastDayOfMonth(LocalDateTime date) {
        return date.getDayOfMonth() >= 28 && date.plusDays(1).getMonthValue() != date.getMonthValue();
    }
}
