package com.realstate.habitar.domain.ports.role;

import java.util.Optional;

public interface RolePort {
    Optional<Object> findRoleByName(String name);
}
