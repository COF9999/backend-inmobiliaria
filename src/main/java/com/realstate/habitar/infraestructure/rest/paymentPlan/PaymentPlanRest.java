package com.realstate.habitar.infraestructure.rest.paymentPlan;

import com.realstate.habitar.application.components.FunnelRentComponent;
import com.realstate.habitar.application.usecases.paymentplan.PaymentPlanService;
import com.realstate.habitar.domain.dtos.paymentPlan.WrapperPaymentPlanDto;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payment-plan")
public class PaymentPlanRest {

    private final PaymentPlanService paymentPlanService;

    private final FunnelRentComponent funnelRentComponent;

    public PaymentPlanRest(PaymentPlanService paymentPlanService, FunnelRentComponent funnelRentComponent) {
        this.paymentPlanService = paymentPlanService;
        this.funnelRentComponent = funnelRentComponent;
    }

    @GetMapping("/list")
    public WrapperPaymentPlanDto wrapperPaymentPlanDto(@RequestParam(name = "mixId") String mixId){
        return paymentPlanService.paymentPlansByCandidateMixId(mixId);
    }


}
