package com.realstate.habitar.domain;

import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;

import java.util.List;
import java.util.Map;

public interface PipelineHandler {

    PipelineType pipelineKey();

    void handle(Map<String, List<HubspotDealDtoApp>> dealsByOwner);

    void generateReport(Map<String,List<HubspotDealDtoApp>> dealsByOwner);
}
