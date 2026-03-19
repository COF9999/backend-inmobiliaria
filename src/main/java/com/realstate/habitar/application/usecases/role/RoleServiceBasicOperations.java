package com.realstate.habitar.application.usecases.role;

import com.realstate.habitar.infraestructure.classes.model.Role;

import java.util.Optional;

public interface RoleServiceBasicOperations {
    Optional<Role> findRoleByName(String nameRole);
}
