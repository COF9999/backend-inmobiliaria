package com.realstate.habitar.application.usecases.dealLiquidate;

import com.realstate.habitar.domain.dtos.dealLiquidate.DealLiquidateResponseDto;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;
import com.realstate.habitar.infraestructure.classes.model.User;
import java.util.List;



public interface DealLiquidateService {
    DealLiquidate createDealLiquidation(HubspotDealDtoApp hubspotDealDtoApp, List<User> setUsers);

    List<DealLiquidateResponseDto> getDealLiquidateByStatus(StatesObjects statesObjects);

    DealLiquidateResponseDto convertTo(DealLiquidate dealLiquidate);
}
