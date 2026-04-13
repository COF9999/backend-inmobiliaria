package com.realstate.habitar.application.usecases.dealRegistry;

import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;

import java.util.List;
import java.util.Optional;

public interface DealRegistryService {

    Optional<DealRegistry> findDealUserId(String dealUserId);

    boolean findNotIsPartiallyRegister(String keyDealRegister);

    List<DealRegisterDto> selectDealsRegister(Byte state);

    void executeTransition(DealMasterBifurcationRecord dealMasterBifurcationRecord);

    DealRegisterDto convertTo(DealRegistry dealRegistry);
}
