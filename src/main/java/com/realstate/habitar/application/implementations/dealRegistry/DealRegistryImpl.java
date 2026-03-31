package com.realstate.habitar.application.implementations.dealRegistry;

import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.adapters.interfaces.dealRegistry.DealRegistryRepository;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DealRegistryImpl implements DealRegistryService {

    private final DealRegistryRepository dealRegistryRepository;

    public DealRegistryImpl(DealRegistryRepository dealRegistryRepository) {
        this.dealRegistryRepository = dealRegistryRepository;
    }

    @Override
    public boolean findNotIsPartiallyRegister(String keyDealRegister) {
        Set<Byte> includeValues = StatesObjects.getSetpartiallyNotRegister();
        return dealRegistryRepository.findNotIsPartiallyRegister(keyDealRegister,includeValues);
    }

    @Transactional
    public List<DealRegisterDto> selectDealsRegister(Byte state){
        return dealRegistryRepository.selectDealsRegister(state)
                .stream()
                .map(this::convertTo)
                .toList();
    }

    @Override
    public DealRegisterDto convertTo(DealRegistry dealRegistry) {
        return new DealRegisterDto(dealRegistry.getDealUserId(),
                dealRegistry.getPipelineType(),
                dealRegistry.getHubId(),
                dealRegistry.getAmount(),
                dealRegistry.getClosedAt()
        );
    }

    @Override
    public void cancelDealRegistry(String hubId) {

    }


}
