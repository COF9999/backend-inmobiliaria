package com.realstate.habitar.domain.dtos.paymentPlan;

import com.realstate.habitar.domain.dtos.dealLiquidate.DealLiquidateResponseDto;

import java.util.List;

public record WrapperPaymentPlanDto(DealLiquidateResponseDto dealLiquidateResponseDto,List<PaymentPlanSimpleDto> paymentPlanSimpleDto) {
}
