package com.realstate.habitar.infraestructure.adapters.interfaces;

import com.realstate.habitar.global.infraestructure.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
