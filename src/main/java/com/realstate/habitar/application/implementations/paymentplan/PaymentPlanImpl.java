package com.realstate.habitar.application.implementations.paymentplan;

import com.realstate.habitar.application.usecases.paymentplan.PaymentPlanService;
import com.realstate.habitar.infraestructure.adapters.interfaces.paymentPlan.PaymentPlanRepository;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentPlanImpl implements PaymentPlanService {

    private final PaymentPlanRepository paymentPlanRepository;

    public PaymentPlanImpl(PaymentPlanRepository paymentPlanRepository) {
        this.paymentPlanRepository = paymentPlanRepository;
    }

    @Override
    public void createPaymentPlan() {

    }

    @Override
    public void batchPaymentPlan(List<PaymentPlan> paymentPlanList) {
        paymentPlanRepository.saveAll(paymentPlanList);
    }
}
