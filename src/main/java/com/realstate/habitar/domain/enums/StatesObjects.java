package com.realstate.habitar.domain.enums;

import com.realstate.habitar.domain.dispactchers.PipelineType;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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


    private static final Set<Byte> SetpartiallyNotRegister = Arrays.stream(values())
            .filter(statesObjects -> Objects.equals(statesObjects.state, NEUTRO.getState()) || Objects.equals(statesObjects.state,CANCEL.getState()))
            .map(StatesObjects::getState)
            .collect(Collectors.toUnmodifiableSet());

    public static Set<Byte> getSetpartiallyNotRegister(){
        return StatesObjects.SetpartiallyNotRegister;
    }


    public Byte getState() {
        return state;
    }
}
