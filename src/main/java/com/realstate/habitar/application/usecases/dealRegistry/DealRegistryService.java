package com.realstate.habitar.application.usecases.dealRegistry;

import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;

import java.util.List;

public interface DealRegistryService {
    boolean findNotIsPartiallyRegister(String keyDealRegister);

    List<DealRegisterDto> selectDealsRegister(Byte state);

    DealRegisterDto convertTo(DealRegistry dealRegistry);

    void cancelDealRegistry(String hubId);
}
