package com.realstate.habitar.infraestructure.adapters.interfaces;

import java.math.BigDecimal;

public interface UserLiquidationRepository {
    void addAmount(String hubId, BigDecimal amountUser);

    boolean isExistUserLiquidation(String hubId);

    Long getIdUserLiquidation(String hubId);
}
