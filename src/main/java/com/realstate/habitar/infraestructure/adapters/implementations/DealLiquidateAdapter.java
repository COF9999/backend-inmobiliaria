package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.dealLiquidate.DealLiquidateRepository;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DealLiquidateAdapter implements DaoCrudPort<DealLiquidate>,DealLiquidateRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DealLiquidate> selectAll() {
        return List.of();
    }

    @Override
    public Optional<DealLiquidate> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<DealLiquidate> create(DealLiquidate object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<DealLiquidate> update(DealLiquidate object) {
        return Optional.empty();
    }

    @Override
    public Optional<DealLiquidate> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public List<DealLiquidate> getDealsByStatus(StatesObjects statesObjects) {
        return entityManager.createQuery("SELECT dealliquidate FROM DealLiquidate dealliquidate WHERE dealliquidate.state = :state",DealLiquidate.class)
                .setParameter("state",statesObjects)
                .getResultList();
    }
}
