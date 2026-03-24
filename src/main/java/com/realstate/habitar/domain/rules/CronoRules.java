package com.realstate.habitar.domain.rules;

import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class CronoRules {

    private static ZoneId zone = ZoneId.of("GMT-5");

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
                .atZone(ZoneId.of("GMT-5"))
                .toInstant()
                .toEpochMilli();
    }
}
