package com.realstate.habitar.infraestructure.adapters.interfaces.user;

import com.realstate.habitar.infraestructure.classes.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
