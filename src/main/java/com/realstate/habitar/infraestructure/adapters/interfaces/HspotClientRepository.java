package com.realstate.habitar.infraestructure.adapters.interfaces;

import com.realstate.habitar.application.usecases.hubspot.HubspotCommand;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotDealKeyRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;


import java.util.List;
import java.util.Map;

public interface HspotClientRepository  {

    List<HubspotDealDtoApp> findDeals(List<Map<String,Object>> mapList);

    HubspotDealDtoApp findDealByPrincipalInfo(HubSpotDealKeyRecord hubSpotDealKeyRecord);

    Map<String, Object> getOwnerByHubIDstatus(String hubId);
}
