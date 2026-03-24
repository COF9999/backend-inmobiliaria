package com.realstate.habitar.domain.dtos.hubspot;

import java.util.Map;

public record HubspotDealDtoApp(String id,
                                String ownerId,
                                String nameUser,
                                String pipelineType,
                                Map<String,String>properties
) {
}
