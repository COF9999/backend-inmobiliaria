package com.realstate.habitar.domain.dtos.hubspot;

public record HubSpotDealEventRecord(
         String subscriptionType,
         Long objectId,
         String propertyName,
         Long eventId,
         String dealStageId,
         String pipelineId,
         String changeSource,
         Long occurredAt
) {
}
