package com.realstate.habitar.application.implementations.dealRegistry;

import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.adapters.interfaces.dealRegistry.DealRegistryRepository;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import com.realstate.habitar.utils.ThrowableActions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
public class DealRegistryImpl implements DealRegistryService {

    private final DealRegistryRepository dealRegistryRepository;

    private final Map<StatesObjects, BiConsumer<DealRegistry, DealMasterBifurcationRecord>> mapActionsDealRegister = new EnumMap<>(StatesObjects.class);

    public DealRegistryImpl(DealRegistryRepository dealRegistryRepository) {
        this.dealRegistryRepository = dealRegistryRepository;
        this.mapActionsDealRegister.put(StatesObjects.CANCEL,this::cancelDealRegister);
        this.mapActionsDealRegister.put(StatesObjects.SUCCESFULLY,this::succesfullyDealRegister);
    }

    @Override
    public Optional<DealRegistry> findDealUserId(String dealUserId) {
        return dealRegistryRepository.findByDealUserId(dealUserId);
    }

    @Override
    public boolean findNotIsPartiallyRegister(String keyDealRegister) {
        Set<StatesObjects> includeValues = StatesObjects.getSetpartiallyNotRegister();
        return dealRegistryRepository.findNotIsPartiallyRegister(keyDealRegister,includeValues);
    }


    @Transactional
    public List<DealRegisterDto> selectDealsRegister(Byte state){
        return dealRegistryRepository.selectDealsRegister(state)
                .stream()
                .map(this::convertTo)
                .toList();
    }


    private void succesfullyDealRegister(DealRegistry dealRegistry,DealMasterBifurcationRecord dealMasterBifurcationRecord){
        dealRegistry.setState(dealMasterBifurcationRecord.statesObjects());
    }


    private void cancelDealRegister(DealRegistry dealRegistry,DealMasterBifurcationRecord dealMasterBifurcationRecord){
        dealRegistry.setState(dealMasterBifurcationRecord.statesObjects());
    }

    @Override
    @Transactional
    public void executeTransition(DealMasterBifurcationRecord dealMasterBifurcationRecord) {
        DealRegistry dealRegistry = dealRegistryRepository.findByDealUserId(dealMasterBifurcationRecord.dealId())
                .orElseThrow(()-> new IllegalArgumentException("Deal Register not exits"));

        BiConsumer<DealRegistry,DealMasterBifurcationRecord> biConsumer = mapActionsDealRegister.get(dealMasterBifurcationRecord.statesObjects());
        boolean isValidTransition = dealRegistry
                .getState()
                .isValidTransition(dealMasterBifurcationRecord.statesObjects());

        if (biConsumer==null || !isValidTransition){
            ThrowableActions.launchRuntimeExeption(()-> new IllegalArgumentException("Error Proccesing executeChange"));
        }

        biConsumer.accept(dealRegistry,dealMasterBifurcationRecord);
    }


    @Override
    public DealRegisterDto convertTo(DealRegistry dealRegistry) {
        return new DealRegisterDto(dealRegistry.getDealUserId(),
                dealRegistry.getNameUser(),
                dealRegistry.getPipelineType(),
                dealRegistry.getHubId(),
                dealRegistry.getAmount(),
                dealRegistry.getClosedAt()
        );
    }

}
