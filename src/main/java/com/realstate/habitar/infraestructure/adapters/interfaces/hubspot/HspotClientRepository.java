package com.realstate.habitar.infraestructure.adapters.interfaces.hubspot;

import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;


import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface HspotClientRepository  {

    List<HubspotDealDtoApp> findDeals(List<Map<String,Object>> mapList);

    <T> HubspotDealDtoApp findDealByPrincipalInfo(Supplier<T> supplierHubId,Supplier<List<String>> supplierListProperties);

    Map<String, Object> getOwnerByHubIDstatus(String hubId);
}
