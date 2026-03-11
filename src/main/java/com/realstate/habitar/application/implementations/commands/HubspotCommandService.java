package com.realstate.habitar.application.implementations.commands;

import com.realstate.habitar.application.usecases.hubspot.HubspotCommand;
import com.realstate.habitar.infraestructure.adapters.interfaces.HspotClientRepository;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class HubspotCommandService implements HubspotCommand {

    private final HspotClientRepository hubspotClientRepository;

    public HubspotCommandService(HspotClientRepository hubspotClientRepository) {
        this.hubspotClientRepository = hubspotClientRepository;
    }

    @Override
    public Map<String, Object> findOwnerByHubId(String hubId) {
       // return hubspotClientRepository.findDeal(hubId);
        return null;
    }
}
