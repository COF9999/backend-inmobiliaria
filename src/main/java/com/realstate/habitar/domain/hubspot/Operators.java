package com.realstate.habitar.domain.hubspot;

import java.util.Map;
import java.util.function.Predicate;

public enum Operators {

    EQ("EQ"),
    GT("GT"),
    LT("LT"),
    GTE("GTE"),
    LTE("LTE");

    private String operation;

    Operators(String operation){
        this.operation = operation;
    }
    public static  <T extends Comparable<T>> Map<String,Object> build(String propertyName,String operator, T target){
        return switch (operator){
            case "=="-> ComparatorHubSpot.instanceSingleQuery(propertyName, EQ.operation, target);
            case ">" -> ComparatorHubSpot.instanceSingleQuery(propertyName, GT.operation, target);
            case "<" -> ComparatorHubSpot.instanceSingleQuery(propertyName, LT.operation, target);
            case ">=" -> ComparatorHubSpot.instanceSingleQuery(propertyName,GTE.operation, target);
            case "<=" -> ComparatorHubSpot.instanceSingleQuery(propertyName, LTE.operation, target);
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };
    }

}
