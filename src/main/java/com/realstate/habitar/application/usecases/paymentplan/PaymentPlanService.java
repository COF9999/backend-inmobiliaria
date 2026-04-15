package com.realstate.habitar.application.usecases.paymentplan;

import com.realstate.habitar.domain.dtos.paymentPlan.WrapperPaymentPlanDto;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public interface PaymentPlanService {
    void batchPaymentPlan(List<PaymentPlan> paymentPlanList);

    PaymentPlan findByUuid(String uuid);

    WrapperPaymentPlanDto paymentPlansByCandidateMixId(String candidateMixId);

    <T1,R2> R2 convertTo(T1 t1, Function<T1,R2> functionMaster);
}
