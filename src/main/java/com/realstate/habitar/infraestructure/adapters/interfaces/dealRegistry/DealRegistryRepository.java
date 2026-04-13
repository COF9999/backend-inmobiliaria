package com.realstate.habitar.infraestructure.adapters.interfaces.dealRegistry;

import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DealRegistryRepository {
    boolean findNotIsPartiallyRegister(String idDealRegistry, Set<StatesObjects> includeValues);

    List<DealRegistry> selectDealsRegister(Byte state);

    Optional<DealRegistry> findByDealUserId(String dealUserId);
}
