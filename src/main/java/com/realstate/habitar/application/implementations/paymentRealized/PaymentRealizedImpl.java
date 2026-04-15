package com.realstate.habitar.application.implementations.paymentRealized;

import com.realstate.habitar.application.usecases.paymentRealized.PaymentRealizedService;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.classes.model.PaymentRealized;
import org.springframework.stereotype.Service;

@Service
public class PaymentRealizedImpl implements PaymentRealizedService {

    private final DaoCrudPort<PaymentRealized> paymentRealizedDaoCrudPort;

    public PaymentRealizedImpl(DaoCrudPort<PaymentRealized> paymentRealizedDaoCrudPort) {
        this.paymentRealizedDaoCrudPort = paymentRealizedDaoCrudPort;
    }

    @Override
    public void createPaymentRealized(PaymentRealized paymentRealized) {
        paymentRealizedDaoCrudPort.create(paymentRealized);
    }
}
