package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.ProcessedDealRepository;
import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public class ProcessedDealsApadter implements DaoCrudPort<ProcessedDeal>,ProcessedDealRepository {


    @PersistenceContext
    private EntityManager entityManager;


    public boolean isExistKey(String key){
        Long count = entityManager.createQuery("SELECT COUNT(p) FROM ProcessedDeal p WHERE p.dealId = :key", Long.class)
                .setParameter("key", key)
                .getSingleResult();
         return count > 0;
    }

    @Transactional(readOnly = true)
    public boolean notIsProcessed(String key){
        Long count = entityManager.createQuery("SELECT COUNT(p) FROM ProcessedDeal p WHERE p.dealId = :key", Long.class)
                .setParameter("key", key)
                .getSingleResult();
        return count == 0;
    }

    @Override
    public List<ProcessedDeal> selectAll() {
        return List.of();
    }

    @Override
    public Optional<ProcessedDeal> get(Long id) {
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<ProcessedDeal> create(ProcessedDeal object) {
        entityManager.persist(object);
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<ProcessedDeal> update(ProcessedDeal object) {
        return Optional.empty();
    }

    @Override
    public Optional<ProcessedDeal> delete(Long id) {
        return Optional.empty();
    }
}
