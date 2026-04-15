package com.realstate.habitar.application.implementations.paymentplan;

import com.realstate.habitar.application.usecases.dealLiquidate.DealLiquidateService;
import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.application.usecases.paymentplan.PaymentPlanService;
import com.realstate.habitar.application.usecases.user.UserService;
import com.realstate.habitar.domain.dtos.dealLiquidate.DealLiquidateResponseDto;
import com.realstate.habitar.domain.dtos.paymentPlan.PaymentPlanMixDto;
import com.realstate.habitar.domain.dtos.paymentPlan.PaymentPlanSimpleDto;
import com.realstate.habitar.domain.dtos.paymentPlan.WrapperPaymentPlanDto;
import com.realstate.habitar.domain.dtos.user.SimpleUserDto;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.paymentPlan.PaymentPlanRepository;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;
import com.realstate.habitar.infraestructure.classes.model.PaymentRealized;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PaymentPlanImpl implements PaymentPlanService {

    private final PaymentPlanRepository paymentPlanRepository;

    private final UserService userService;

    private final DealRegistryService dealRegistryService;


    public PaymentPlanImpl(PaymentPlanRepository paymentPlanRepository, UserService userService, DealRegistryService dealRegistryService) {
        this.paymentPlanRepository = paymentPlanRepository;
        this.userService = userService;
        this.dealRegistryService = dealRegistryService;
    }

    @Override
    public void batchPaymentPlan(List<PaymentPlan> paymentPlanList) {
        paymentPlanRepository.saveAll(paymentPlanList);
    }

    @Override
    public PaymentPlan findByUuid(String uuid) {
        return paymentPlanRepository.findPaymentPlanByUuid(uuid).orElseThrow();
    }

    @Override
    public WrapperPaymentPlanDto paymentPlansByCandidateMixId(String candidateMixId) {

        List<PaymentPlanMixDto> plansList = paymentPlanRepository.findByDealUserId(candidateMixId);

        DealLiquidateResponseDto dealLiquidateResponseDto = new DealLiquidateResponseDto(
                dealRegistryService.convertTo( plansList.getFirst().paymentPlan().getDealLiquidate().getDealRegistry()),
                plansList.getFirst().paymentPlan().getDealLiquidate().getState()
        );


        Function<PaymentPlanMixDto,PaymentPlanSimpleDto> functionPlan = (p)-> new PaymentPlanSimpleDto(
                p.paymentPlan().getUuid(),
                p.paymentPlan().getTraceFirstDate(),
                p.paymentPlan().getTraceSecondDate(),
                userService.converTo(p.paymentPlan().getUser(), u -> new SimpleUserDto(u.getHubId(), u.getName())),
                p.isRealizedPay()
        )
        ;

        List<PaymentPlanSimpleDto> planSimpleDtoList = plansList
                .parallelStream()
                .map(p-> convertTo(p,functionPlan))
                .toList();

        return new WrapperPaymentPlanDto(dealLiquidateResponseDto,planSimpleDtoList);
    }


    public <T1,R2> R2 convertTo(T1 t1, Function<T1,R2> functionMaster) {
        return functionMaster.apply(t1);
    }


}
