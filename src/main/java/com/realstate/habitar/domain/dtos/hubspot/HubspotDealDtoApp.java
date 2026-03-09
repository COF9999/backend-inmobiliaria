package com.realstate.habitar.domain.dtos.hubspot;

import com.realstate.habitar.domain.PipelineType;

import java.util.Map;

public record HubspotDealDtoApp(String id,
                                String ownerId,
                                String pipelineType,
                                Map<String,String>properties
) {
}
