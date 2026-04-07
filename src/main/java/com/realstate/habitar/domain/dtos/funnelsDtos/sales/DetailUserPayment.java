package com.realstate.habitar.domain.dtos.funnelsDtos.sales;

import java.math.BigDecimal;

public record DetailUserPayment(String nameUser,
                                String hubId ,
                                BigDecimal amountToPay,
                                BigDecimal commisionPercentage,
                                BigDecimal operationPercentage
                               ) {}
