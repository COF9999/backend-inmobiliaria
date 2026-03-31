package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.dealRegistry.DealRegistryRepository;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class DealRegistryAdapter implements DaoCrudPort<DealRegistry>,DealRegistryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DealRegistry> selectAll() {
        return List.of();
    }

    @Override
    public Optional<DealRegistry> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<DealRegistry> create(DealRegistry object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<DealRegistry> update(DealRegistry object) {
        return Optional.empty();
    }

    @Override
    public Optional<DealRegistry> delete(Long id) {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean findNotIsPartiallyRegister(String idDealRegistry,Set<Byte> includeValues) {


        Long count = entityManager.createQuery("SELECT COUNT(d) FROM DealRegistry d WHERE d.id = :key and d.state IN :includevalues", Long.class)
                .setParameter("key", idDealRegistry)
                .setParameter("includevalues",includeValues)
                .getSingleResult();
        return count == 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealRegistry> selectDealsRegister(Byte state) {
        return entityManager.createQuery("SELECT d FROM DealRegistry d WHERE d.state = :state",DealRegistry.class)
                .setParameter("state",state)
                .getResultList();
    }
}
