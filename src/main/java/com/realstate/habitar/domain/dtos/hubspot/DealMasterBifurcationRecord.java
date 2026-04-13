package com.realstate.habitar.domain.dtos.hubspot;

import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;

public record DealMasterBifurcationRecord(String dealId, HubspotEnum hubspotEnum, String typeMethod, StatesObjects statesObjects) {
}
