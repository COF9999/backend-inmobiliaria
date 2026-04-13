package com.realstate.habitar.application.usecases.paymentplan;

import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;

import java.util.List;

public interface PaymentPlanService {
    void createPaymentPlan();

    void batchPaymentPlan(List<PaymentPlan> paymentPlanList);
}
