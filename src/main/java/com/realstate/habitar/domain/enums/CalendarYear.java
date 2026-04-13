package com.realstate.habitar.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum CalendarYear {
    JANUARY((byte) 1),
    FEBRUARY((byte) 2),
    MARCH((byte) 3),
    APRIL((byte) 4),
    MAY((byte) 5),
    JUNE((byte) 6),
    JULY((byte) 7),
    AGUST((byte) 8),
    SEPTEMBER((byte) 9),
    OBTOBER((byte) 10),
    NOVEMBER((byte) 11),
    DECEMBER((byte) 12);

    private final Byte value;

    CalendarYear(Byte value){
        this.value = value;
    }

    @JsonValue
    public Byte getValue() {
        return value;
    }

    private final static Map<Byte,CalendarYear> mapCalendarYear = new HashMap<>();

    static {
        for (CalendarYear c:values()) mapCalendarYear.put(c.value,c);
    }

    @JsonCreator
    public static CalendarYear getMonthValueFromMap(Byte monthValue){
        CalendarYear calendarYear = mapCalendarYear.get(monthValue);
        if (calendarYear==null){
            throw new IllegalArgumentException("Value of state invalid");
        }
        return calendarYear;
    }
}
