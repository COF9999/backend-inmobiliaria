package com.realstate.habitar.infraestructure.adapters.implementations;


import com.realstate.habitar.domain.ports.user.UserDaoPort;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.classes.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserEmanagerAdapter implements DaoCrudPort<User>, UserDaoPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void activeDeleted(Long id) {
        User user = entityManager.find(User.class,id);
       // user.setIsActive(false);
        update(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUser(String hubId) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.hubId = :hubId", User.class)
                .setParameter("hubId", hubId)
                .getResultList();

        // Retornamos un Optional para obligar al servicio a manejar el caso "vacío"
        return results.stream().findFirst();
    }


    public boolean isUserActive(String email) {
        List<Boolean> results = entityManager.createQuery(
                        "SELECT u.isActive FROM User u WHERE u.email = :email", Boolean.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();

        if (results.isEmpty()){
            throw new IllegalArgumentException("User not found with that email");
        }

        return results.get(0);
    }

    @Override
    public List<User> getListUsers() {
        return entityManager.createQuery(
                        "SELECT u FROM User u",User.class)
                .getResultList();
    }


    @Override
    public List<User> selectAll() {
        return List.of();
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(entityManager.find(User.class,id));
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<User> create(User object) {
        entityManager.persist(object);
        return Optional.ofNullable(object);
    }


    @Override
    public Optional<User> update(User object) {
        return Optional.ofNullable(entityManager.merge(object));
    }


    @Override
    public Optional<User> delete(Long id) {
        return null;
    }
}