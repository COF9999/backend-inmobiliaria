package com.realstate.habitar.domain.rules;

import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CronoRules {

    private static long milesecondsByDayAgo(Long days){
        long now = Instant.now().toEpochMilli();
        return Instant.now()
                .minus(days, ChronoUnit.DAYS)
                .toEpochMilli();
    }

    public static LiquidationTimeRecord getMileSecondsTime(LiquidationTimeRecord liquidationTimeRecord){
        Integer year;
        Integer month;
        Integer startDay;
        Integer endDay;
        Long mileSecondsStart;
        Long mileSecondsEnd;

        if (liquidationTimeRecord.onlyDay()!=null){
            System.out.println("ENTROROROORO");

            Integer onlyDay = liquidationTimeRecord.onlyDay();
            if (onlyDay<=0 || onlyDay>31){
                throw new IllegalArgumentException("El parametro para los días no esta en un rango valido");
            }
            mileSecondsStart = milesecondsByDayAgo(Long.valueOf(onlyDay));
            mileSecondsEnd = System.currentTimeMillis();
            return new LiquidationTimeRecord.BuilderLiquidationTime()
                    .setOnlyDay(onlyDay)
                    .setMilesecondsStartDay(mileSecondsStart)
                    .setMilesecondsEndDay(mileSecondsEnd)
                    .build();


        }else {
            year = liquidationTimeRecord.year();
            month = liquidationTimeRecord.month();
            startDay = liquidationTimeRecord.startDay();
            endDay = liquidationTimeRecord.endDay();
            LocalDateTime ldtStarDay = generateLocalDateTime(year,month,startDay,0,0);
            LocalDateTime ldtEndDay = generateLocalDateTime(year,month,endDay,0,0);
            mileSecondsStart = mileSecondsOfDate(ldtStarDay);
            mileSecondsEnd = mileSecondsOfDate(ldtEndDay);
            return new LiquidationTimeRecord.BuilderLiquidationTime()
                    .setMilesecondsStartDay(mileSecondsStart)
                    .setMilesecondsEndDay(mileSecondsEnd)
                    .build();
        }
    }


    private static LocalDateTime generateLocalDateTime(int year, int month, int startDay, int hour, int minut) {
       return LocalDateTime.of(year,month,startDay,hour,minut);
    }

    private static long mileSecondsOfDate(LocalDateTime ldtDate){
        return ldtDate.atZone(ZoneId.of("GMT-5"))
                .toInstant()
                .toEpochMilli();
    }
}
