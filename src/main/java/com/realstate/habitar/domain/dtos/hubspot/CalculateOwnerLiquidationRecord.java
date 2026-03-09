package com.realstate.habitar.domain.dtos.hubspot;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record CalculateOwnerLiquidationRecord(Map<String, BigDecimal> resultOperation,
                                              Set<HubspotDealDtoApp> dealsProccesed) {
}
