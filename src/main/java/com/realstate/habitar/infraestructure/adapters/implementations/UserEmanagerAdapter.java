package com.realstate.habitar.infraestructure.adapters.implementations;


import com.realstate.habitar.domain.ports.user.UserDaoPort;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.global.infraestructure.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
    public Optional<Object> findByEmail(String email) {
        try {
            Query query =  entityManager.createQuery("select u from User u where u.email=?1", User.class);
            query.setParameter(1, email);
            return Optional.ofNullable(query.getSingleResult());
        }catch (NoResultException e){
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