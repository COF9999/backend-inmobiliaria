package com.realstate.habitar.domain.ports.role;

import com.realstate.habitar.infraestructure.classes.model.Role;

import java.util.Optional;

public interface RolePort {
    Optional<Role> findRoleByName(String name);
}
