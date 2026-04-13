package com.realstate.habitar.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.realstate.habitar.domain.dispactchers.PipelineType;

import java.util.*;
import java.util.stream.Collectors;

public enum StatesObjects {
    REFUSED((byte) -2),
    CANCEL((byte) -1),
    NEUTRO((byte) 0),
    OPEN((byte) 1),
    SUCCESFULLY((byte) 5);

    private final Byte state;

    StatesObjects(Byte state) {
        this.state = state;
    }

    @JsonValue
    public Byte getState() {
        return state;
    }

    private static final Set<StatesObjects> SetpartiallyNotRegister;

    private static final Map<Byte,StatesObjects> mapStateObjects = new HashMap<>();

    static {
        for (StatesObjects s:values()) mapStateObjects.put(s.state,s);

        SetpartiallyNotRegister = Arrays.stream(values())
                .filter(statesObjects -> Objects.equals(statesObjects.state, NEUTRO.getState()) || Objects.equals(statesObjects.state,CANCEL.getState()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @JsonCreator
    public static StatesObjects fromValue(Object value) {
        if (value == null) return null;

        if (value instanceof Number num) {
            // Forzamos a byte SIEMPRE para asegurar match en el Map<Byte, ...>
            byte b = num.byteValue();
            StatesObjects res = mapStateObjects.get(b);
            if (res == null) throw new IllegalArgumentException("No hay Enum para el byte: " + b);
            return res;
        }
        if (value instanceof String str) {
            return StatesObjects.valueOf(str.toUpperCase());
        }
        throw new IllegalArgumentException("Valor de estado inválido: " + value);
    }

    public boolean isValidTransition(StatesObjects newStateObject){
        return switch (this){
            case REFUSED -> newStateObject.equals(NEUTRO);
            case CANCEL -> newStateObject.equals(REFUSED) || newStateObject.equals(OPEN);
            case NEUTRO -> false;
            case OPEN -> newStateObject.equals(CANCEL) || newStateObject.equals(SUCCESFULLY);
            case SUCCESFULLY -> false;
        };
    }

    public static Set<StatesObjects> getSetpartiallyNotRegister(){
        return StatesObjects.SetpartiallyNotRegister;
    }



}
