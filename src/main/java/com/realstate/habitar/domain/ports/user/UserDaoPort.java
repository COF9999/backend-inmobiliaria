package com.realstate.habitar.domain.ports.user;


import com.realstate.habitar.global.infraestructure.models.User;

import java.util.Optional;

public interface UserDaoPort {
    Optional<User> findByEmail(String identification);

    void activeDeleted(Long id);

    Optional<User> getUser(String hubId);

    boolean isUserActive(String email);
}
