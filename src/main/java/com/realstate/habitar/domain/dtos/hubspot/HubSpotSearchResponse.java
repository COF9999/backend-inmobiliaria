package com.realstate.habitar.domain.dtos.hubspot;

import lombok.Data;

import java.util.List;

@Data
public class HubSpotSearchResponse {

    private List<HubSpotDealDto> results;
}