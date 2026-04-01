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


    private static final Set<Byte> SetpartiallyNotRegister = Arrays.stream(values())
            .filter(statesObjects -> Objects.equals(statesObjects.state, NEUTRO.getState()) || Objects.equals(statesObjects.state,CANCEL.getState()))
            .map(StatesObjects::getState)
            .collect(Collectors.toUnmodifiableSet());



    private static final Map<Byte,StatesObjects> mapStateObjects = new HashMap<>();

    @JsonCreator
    public static StatesObjects getStateFromMap(Byte state){
       StatesObjects statesObjects = mapStateObjects.get(state);
       if (statesObjects==null){
           throw new IllegalArgumentException("Value of state invalid");
       }
       return statesObjects;
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

    public static Set<Byte> getSetpartiallyNotRegister(){
        return StatesObjects.SetpartiallyNotRegister;
    }




    static {
        for (StatesObjects s:values()) mapStateObjects.put(s.state,s);
    }
}
