package com.realstate.habitar.infraestructure.adapters.interfaces.dealLiquidate;

import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;

import java.util.List;

public interface DealLiquidateRepository {
    List<DealLiquidate> getDealsByStatus(StatesObjects statesObjects);
}
