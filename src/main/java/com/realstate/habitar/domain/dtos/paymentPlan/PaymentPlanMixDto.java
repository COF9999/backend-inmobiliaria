package com.realstate.habitar.domain.dtos.paymentPlan;

import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;

public record PaymentPlanMixDto(PaymentPlan paymentPlan,boolean isRealizedPay) {
}
