package com.realstate.habitar.infraestructure.adapters.interfaces.paymentPlan;

import com.realstate.habitar.domain.dtos.paymentPlan.PaymentPlanMixDto;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;

import java.util.List;
import java.util.Optional;

public interface PaymentPlanRepository {
    void saveAll(List<PaymentPlan> paymentPlanList);

    List<PaymentPlanMixDto> findByDealUserId(String candidateMixId);

    Optional<PaymentPlan> findPaymentPlanByUuid(String uuid);
}
