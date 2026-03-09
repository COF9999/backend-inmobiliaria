package com.realstate.habitar.domain.dtos.hubspot;


import java.util.Map;


public record HubSpotDealDto(String id,Map<String,String> properties) { }
