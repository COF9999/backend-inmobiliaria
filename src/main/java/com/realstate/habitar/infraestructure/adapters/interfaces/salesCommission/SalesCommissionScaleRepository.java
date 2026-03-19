package com.realstate.habitar.infraestructure.adapters.interfaces.salesCommission;

import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;

import java.util.List;
import java.util.Optional;

public interface SalesCommissionScaleRepository {
    Optional<SalesCommissionScale> findByRange(Long value);

    List<SalesCommissionScale> getAllCommisionsSalesScale();
}
