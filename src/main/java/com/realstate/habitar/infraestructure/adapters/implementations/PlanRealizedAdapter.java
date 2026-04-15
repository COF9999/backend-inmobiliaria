package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.classes.model.PaymentRealized;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlanRealizedAdapter implements DaoCrudPort<PaymentRealized> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentRealized> selectAll() {
        return List.of();
    }

    @Override
    public Optional<PaymentRealized> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentRealized> create(PaymentRealized object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<PaymentRealized> update(PaymentRealized object) {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentRealized> delete(Long id) {
        return Optional.empty();
    }
}
