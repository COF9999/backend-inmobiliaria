package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.application.usecases.paymentplan.PaymentPlanService;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.paymentPlan.PaymentPlanRepository;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentPlanAdapter implements DaoCrudPort<PaymentPlan>, PaymentPlanRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentPlan> selectAll() {
        return List.of();
    }

    @Override
    public Optional<PaymentPlan> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentPlan> create(PaymentPlan object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<PaymentPlan> update(PaymentPlan object) {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentPlan> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public void saveAll(List<PaymentPlan> paymentPlanList) {
        for (int i = 0; i < paymentPlanList.size(); i++) {
            entityManager.persist(paymentPlanList.get(i));
            // Opcional: Para batching, flush y clear cada 50-100 entidades
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}
