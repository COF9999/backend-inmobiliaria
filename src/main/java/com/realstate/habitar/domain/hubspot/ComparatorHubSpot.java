package com.realstate.habitar.domain.hubspot;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class ComparatorHubSpot {
    public static <T extends Comparable<T>> Map<String, Object> instanceSingleQuery(String propertyName, String operator, T target) {
        return Map.of("propertyName",propertyName,
                "operator",operator,
                "value",target);
    }





}
