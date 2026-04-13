package com.realstate.habitar.domain.dispactchers;

import com.realstate.habitar.domain.dtos.funnelsDtos.ResultDto;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;

import java.util.List;
import java.util.Map;

public interface PipelineHandler {

    PipelineType pipelineKey();

    ResultDto handle(String candidateMixId,
                     Map<String, List<HubspotDealDtoApp>> listDeals,
                     String typeMethodFunnel);
}
