package com.realstate.habitar.application.implementations.dealLiquidate;

import com.realstate.habitar.application.usecases.dealLiquidate.DealLiquidateService;
import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.dealLiquidate.DealLiquidateResponseDto;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.domain.rules.CronoRules;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.dealLiquidate.DealLiquidateRepository;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import com.realstate.habitar.infraestructure.classes.model.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


@Service
public class DealLiquidationImpl implements DealLiquidateService {

    private final DaoCrudPort<DealLiquidate> dealLiquidateDaoCrudPort;

    private final DealLiquidateRepository dealLiquidateRepository;

    private final DealRegistryService dealRegistryService;


    public DealLiquidationImpl(DaoCrudPort<DealLiquidate> dealLiquidateDaoCrudPort, DealLiquidateRepository dealLiquidateRepository, DealRegistryService dealRegistryService) {
        this.dealLiquidateDaoCrudPort = dealLiquidateDaoCrudPort;
        this.dealLiquidateRepository = dealLiquidateRepository;
        this.dealRegistryService = dealRegistryService;
    }

    @Override
    public DealLiquidate createDealLiquidation(HubspotDealDtoApp hubspotDealDtoApp, List<User> setUsers) {
        DealRegistry dealRegistry = dealRegistryService.findDealUserId(hubspotDealDtoApp.id()+"-"+hubspotDealDtoApp.ownerId())
                .orElseThrow();
        LocalDateTime created = CronoRules.currentLocalDatime();
        DealLiquidate dealLiquidate = new DealLiquidate(null,dealRegistry,setUsers,created ,StatesObjects.OPEN);
        dealLiquidateDaoCrudPort.create(dealLiquidate);
        return dealLiquidate;
    }

    @Override
    public List<DealLiquidateResponseDto> getDealLiquidateByStatus(StatesObjects statesObjects) {
        return dealLiquidateRepository.getDealsByStatus(statesObjects)
                .stream()
                .map(this::convertTo)
                .toList();
    }

    @Override
    public DealLiquidateResponseDto convertTo(DealLiquidate dealLiquidate) {
        return new DealLiquidateResponseDto(dealRegistryService.convertTo(dealLiquidate.getDealRegistry()),dealLiquidate.getState());
    }


}
