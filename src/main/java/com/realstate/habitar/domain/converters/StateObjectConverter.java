package com.realstate.habitar.domain.converters;

import com.realstate.habitar.domain.enums.StatesObjects;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StateObjectConverter implements AttributeConverter<StatesObjects,Byte> {

    @Override
    public Byte convertToDatabaseColumn(StatesObjects statesObjects) {
        return (statesObjects == null) ? null : statesObjects.getState();
    }

    @Override
    public StatesObjects convertToEntityAttribute(Byte aByte) {
        return (aByte == null) ? null : StatesObjects.fromValue(aByte);
    }
}
