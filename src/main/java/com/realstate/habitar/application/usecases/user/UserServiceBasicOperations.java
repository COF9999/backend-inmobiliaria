package com.realstate.habitar.application.usecases.user;

import com.realstate.habitar.infraestructure.classes.model.User;

import java.util.Optional;

public interface UserServiceBasicOperations {
    Optional<User> findUserByEmail(String email);

    User update(User user);

    User create(User user);

    Optional<User> findUserById(Long id);
}
