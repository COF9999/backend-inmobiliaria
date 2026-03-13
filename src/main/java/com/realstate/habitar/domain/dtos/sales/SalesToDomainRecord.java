package com.realstate.habitar.domain.dtos.sales;

import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;

import java.math.BigDecimal;
import java.util.List;

public record SalesToDomainRecord(HubspotDealDtoApp hubspotDealDtoApp,
                                  List<SalesCommissionScale> listSalesCommissionScales,
                                  BigDecimal percentageSale,
                                  Boolean isOwner) {
}
