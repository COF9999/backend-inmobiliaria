package com.realstate.habitar.domain.dispactchers;

import com.realstate.habitar.utils.ThrowableActions;

import java.util.Arrays;
import java.util.Set;

public enum TypeMethodFunnel {

    FUNNELSALES("Funnel Sales",Set.of("LIQUIDATE","REPORT")),
    FUNNELRENT("Funnel Rent",Set.of("PLAN","DEAL","REPORT")),
    FUNNELRENTPLACEMENT("Funnel Rent Placement",Set.of("DEAL","REPORT"));

    private final String fieldName;
    private final Set<String> operations;

    TypeMethodFunnel(String fieldName,Set<String> operations) {
        this.fieldName = fieldName;
        this.operations = operations;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static void isIncludeMethod(String fieldName, String typeMethod){
        boolean isInclude = Arrays.stream(values())
                .filter(t-> t.fieldName.equalsIgnoreCase(fieldName))
                .anyMatch(t-> t.operations.contains(typeMethod));
        if(!isInclude){
            ThrowableActions.launchRuntimeExeption(()-> new IllegalArgumentException("Type-method-not exists"));
        }
    }

    public Set<String> getOperations() {
        return operations;
    }
}
