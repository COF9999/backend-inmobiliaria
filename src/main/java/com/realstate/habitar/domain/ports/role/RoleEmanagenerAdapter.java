package com.realstate.habitar.domain.ports.role;

import com.realstate.habitar.infraestructure.classes.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public class RoleEmanagenerAdapter implements RolePort {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Role> findRoleByName(String name) {
           return entityManager
                   .createQuery("select r from Role r where r.name= :name", Role.class)
                    .setParameter("name",name)
                    .getResultStream()
                    .findFirst();
    }
}
