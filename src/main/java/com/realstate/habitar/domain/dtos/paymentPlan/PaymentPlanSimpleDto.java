package com.realstate.habitar.domain.dtos.paymentPlan;

import com.realstate.habitar.domain.dtos.user.SimpleUserDto;

import java.time.LocalDateTime;

public record PaymentPlanSimpleDto(String uuid,LocalDateTime traceFirstDate, LocalDateTime traceSecondDate, SimpleUserDto simpleUserDto,boolean isRealizedPay) {
}
