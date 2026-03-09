package com.realstate.habitar.global.security.specialObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthoritiesCreator {
    @JsonCreator
    public SimpleGrantedAuthoritiesCreator(@JsonProperty("authority") String role){}
}
