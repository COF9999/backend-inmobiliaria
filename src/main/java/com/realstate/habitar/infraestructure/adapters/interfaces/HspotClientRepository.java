package com.realstate.habitar.infraestructure.adapters.interfaces;

import com.realstate.habitar.domain.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;


import java.util.List;
import java.util.Map;

public interface HspotClientRepository {

    List<HubspotDealDtoApp> findDeal(List<Map<String,Object>> mapList);
}
