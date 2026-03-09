package com.realstate.habitar.domain.ports.role;

import com.realstate.habitar.global.infraestructure.models.Role;
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
    public Optional<Object> findRoleByName(String name) {
        try {
            Query query = entityManager.createQuery("select r from Role r where r.name=?1", Role.class);
            query.setParameter(1,name);
            return Optional.of(query.getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }

    }
}
