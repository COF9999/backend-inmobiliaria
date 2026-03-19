package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.userLiquidation.UserLiquidationRepository;
import com.realstate.habitar.infraestructure.classes.model.UserLiquidation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserLiquidationAdapter implements DaoCrudPort<UserLiquidation> , UserLiquidationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addAmount(String hubId, BigDecimal amountUser) {
        System.out.println("HUBID "+hubId+"_"+amountUser);

            Query query = entityManager.createQuery(
                    "UPDATE UserLiquidation u " +
                            "SET u.totalAmount = u.totalAmount + :amountUser " +
                            "WHERE u.user.hubId = :hubId"
            );

            query.setParameter("amountUser",amountUser);
            query.setParameter("hubId", hubId);
            query.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserLiquidation(String hubId) {
        Query query = entityManager.createQuery("SELECT COUNT(ul) FROM UserLiquidation ul where ul.user.hubId=:hubId",Long.class);

        Long count = (Long) query.setParameter("hubId",hubId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Long getIdUserLiquidation(String hubId) {
        try {
            Long id = entityManager.createQuery(
                            "SELECT ul.id FROM UserLiquidation ul where ul.user.hubId = :hubId"
                            ,Long.class)
                    .setParameter("hubId",hubId)
                    .getSingleResult();
            return id;
        }catch (NoResultException e){
          throw new IllegalArgumentException("HubId not exits "+hubId);
        }
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<UserLiquidation> create(UserLiquidation object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<UserLiquidation> update(UserLiquidation object) {
        return Optional.empty();
    }

    @Override
    public Optional<UserLiquidation> delete(Long id) {
        return Optional.empty();
    }


    @Override
    public List<UserLiquidation> selectAll() {
        return List.of();
    }

    @Override
    public Optional<UserLiquidation> get(Long id) {
        return Optional.empty();
    }
}
