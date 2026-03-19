package com.realstate.habitar.application.implementations;

import com.realstate.habitar.application.usecases.role.RoleServiceBasicOperations;
import com.realstate.habitar.domain.ports.role.RolePort;
import com.realstate.habitar.infraestructure.classes.model.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleServiceBasicOperations {

    private final RolePort rolePort;

    public RoleServiceImpl(RolePort rolePort) {
        this.rolePort = rolePort;
    }

    @Override
    public Optional<Role> findRoleByName(String nameRole) {
        return rolePort.findRoleByName(nameRole);
    }
}
