package com.realstate.habitar.domain.dispactchers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum TypeMethodFunnel {

    FUNNELSALES("Funnel Sales",Set.of("DEAL","REPORT")),
    FUNNELRENT("Funnel Rent",Set.of("DEAL","REPORT")),
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

    public static boolean isCludeMethod(String fieldName,String typeMethod){
        return Arrays.stream(values())
                .filter(t-> t.fieldName.equalsIgnoreCase(fieldName))
                .anyMatch(t-> t.operations.contains(typeMethod));
    }

    public Set<String> getOperations() {
        return operations;
    }
}
