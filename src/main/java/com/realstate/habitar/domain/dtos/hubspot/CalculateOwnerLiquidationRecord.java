package com.realstate.habitar.domain.dtos.hubspot;

import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;

import java.util.Map;
import java.util.Set;

public record CalculateOwnerLiquidationRecord(Map<String, DetailResumeAbstract> resultOperation,
                                              Set<HubspotDealDtoApp> dealsProccesed) {
}
