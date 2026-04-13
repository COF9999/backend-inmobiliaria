package com.realstate.habitar.domain.converters;

import com.realstate.habitar.domain.enums.CalendarYear;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CalendarYearConverter implements AttributeConverter<CalendarYear,Byte> {
    @Override
    public Byte convertToDatabaseColumn(CalendarYear calendarYear) {
        return (calendarYear == null) ? null : calendarYear.getValue();
    }

    @Override
    public CalendarYear convertToEntityAttribute(Byte aByte) {
        return (aByte == null) ? null : CalendarYear.getMonthValueFromMap(aByte);
    }
}
