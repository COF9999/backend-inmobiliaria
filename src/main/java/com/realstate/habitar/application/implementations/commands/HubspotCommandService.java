package com.realstate.habitar.application.implementations.commands;

import com.realstate.habitar.application.usecases.hubspot.HubspotCommand;
import com.realstate.habitar.infraestructure.adapters.implementations.HubspotDealClient;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class HubspotCommandService implements HubspotCommand {

    /*
    private final HspotClientRepository hubspotClientRepository;

    public HubspotCommandService(HspotClientRepository hubspotClientRepository) {
        this.hubspotClientRepository = hubspotClientRepository;
    }

     */

    private final HubspotDealClient hubspotDealClient;

    public HubspotCommandService(HubspotDealClient hubspotDealClient) {
        this.hubspotDealClient = hubspotDealClient;
    }

    @Override
    public Map<String, Object> findOwnerByHubId(String hubId) {
       // return hubspotClientRepository.findDeal(hubId);
        //return hubspotClientRepository.getOwnerByHubIDstatus(hubId);
        return null;
    }
}
