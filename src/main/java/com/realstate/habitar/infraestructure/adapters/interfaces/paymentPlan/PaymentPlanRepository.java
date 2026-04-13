package com.realstate.habitar.infraestructure.adapters.interfaces.paymentPlan;

import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;

import java.util.List;

public interface PaymentPlanRepository {
    void saveAll(List<PaymentPlan> paymentPlanList);
}
