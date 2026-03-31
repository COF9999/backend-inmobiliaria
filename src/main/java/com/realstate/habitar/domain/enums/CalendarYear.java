package com.realstate.habitar.domain.enums;

public enum CalendarYear {
    ENERO((byte) 1),
    FEBRERO((byte) 2),
    MARZO((byte) 3),
    ABRIL((byte) 4),
    MAYO((byte) 5),
    JUNIO((byte) 6),
    JULIO((byte) 7),
    AGOSTO((byte) 8),
    SEPTIEMBRE((byte) 9),
    OBTUBRE((byte) 10),
    NOVIEMBRE((byte) 11),
    DICIEMBRE((byte) 12);

    private final Byte value;

    CalendarYear(Byte value){
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }
}
