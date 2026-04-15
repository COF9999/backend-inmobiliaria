package com.realstate.habitar.application.usecases.paymentRealized;

import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;
import com.realstate.habitar.infraestructure.classes.model.PaymentRealized;

public interface PaymentRealizedService {
    void createPaymentRealized(PaymentRealized paymentRealized);
}
