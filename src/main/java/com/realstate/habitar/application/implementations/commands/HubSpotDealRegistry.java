package com.realstate.habitar.application.implementations.commands;

import com.realstate.habitar.domain.dtos.hubspot.HubSpotDealKeyRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class HubSpotDealRegistry {

    private final HspotClientRepository hspotClientRepository;

    private final DaoCrudPort<DealRegistry> dealRegistryDaoCrudPort;

    public HubSpotDealRegistry(HspotClientRepository hspotClientRepository,  DaoCrudPort<DealRegistry> dealRegistryDaoCrudPort) {
        this.hspotClientRepository = hspotClientRepository;
        this.dealRegistryDaoCrudPort = dealRegistryDaoCrudPort;
    }


    @Transactional
    public void createDealRegistry(HubSpotDealKeyRecord hubSpotDealKeyRecord){
        HubspotDealDtoApp hubspotDealDtoApp = hspotClientRepository.findDealByPrincipalInfo(hubSpotDealKeyRecord);
        LocalDateTime closeat= OffsetDateTime
                .parse(hubspotDealDtoApp.properties().get("closeat"))
                .withOffsetSameInstant(ZoneOffset.ofHours(-5))
                .toLocalDateTime();

        DealRegistry dealRegistry = new DealRegistry(
                null,
                hubspotDealDtoApp.id()+"-"+hubspotDealDtoApp.ownerId(),
                hubspotDealDtoApp.pipelineType(),
                Long.parseLong(hubspotDealDtoApp.ownerId())
                ,new BigDecimal(hubspotDealDtoApp.properties().get("amount"))
                ,closeat ,
                StatesObjects.OPEN.getState());

        dealRegistryDaoCrudPort.create(dealRegistry);
    }
}
