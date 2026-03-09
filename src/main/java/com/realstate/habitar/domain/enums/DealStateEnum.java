package com.realstate.habitar.domain.enums;

public enum DealStateEnum {
    CAPTACIONGANADA(""),
    CIERREGANADO("151455346");
    private final String stageValue;

    DealStateEnum(String stageValue){
        this.stageValue = stageValue;
    }

    public String getStageValue(){
        return this.stageValue;
    }
}
