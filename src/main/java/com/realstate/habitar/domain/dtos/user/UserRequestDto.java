package com.realstate.habitar.domain.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realstate.habitar.global.infraestructure.models.Role;

import java.util.List;
import java.util.Set;

public record UserRequestDto(
        Long id,
        String hubId,
        Boolean isActive,
        Set<Role> roles,
        String name,
        String username,
        String email,
        String identification,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        Boolean isAdmin

) {
}
