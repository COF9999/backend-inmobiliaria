package com.realstate.habitar.application.implementations.commands;

import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceNotFound;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class HubSpotDealRegistry {

    private final HspotClientRepository hspotClientRepository;

    private final DaoCrudPort<DealRegistry> dealRegistryDaoCrudPort;

    private final DealRegistryService dealRegistryService;

    public HubSpotDealRegistry(HspotClientRepository hspotClientRepository, DaoCrudPort<DealRegistry> dealRegistryDaoCrudPort, DealRegistryService dealRegistryService) {
        this.hspotClientRepository = hspotClientRepository;
        this.dealRegistryDaoCrudPort = dealRegistryDaoCrudPort;
        this.dealRegistryService = dealRegistryService;
    }

    @Transactional
    public void manipulateCreationDealRegistry(DealMasterBifurcationRecord dealMasterBifurcationRecord){
        HubspotDealDtoApp hubspotDealDtoApp = hspotClientRepository.findDealByPrincipalInfo(dealMasterBifurcationRecord);

        String dealMixHubId = hubspotDealDtoApp.id() + hubspotDealDtoApp.ownerId();

        Optional<DealRegistry> dealRegistryOpt = dealRegistryService.findDealUserId(dealMixHubId);

        if (dealRegistryOpt.isEmpty()){
            createDealRegister(hubspotDealDtoApp);
        }else {
            DealRegistry dealRegisty = dealRegistryOpt.get();
            if (dealRegisty.getState().equals(StatesObjects.CANCEL)){
                dealRegisty.setState(StatesObjects.OPEN);
            }
        }
    }

    private void createDealRegister(HubspotDealDtoApp hubspotDealDtoApp){
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
                StatesObjects.OPEN);

        dealRegistryDaoCrudPort.create(dealRegistry);
    }
}
