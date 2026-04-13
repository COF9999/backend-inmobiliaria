package com.realstate.habitar.infraestructure.adapters.interfaces.funnelrentconfig;

import com.realstate.habitar.infraestructure.classes.model.FunnelRentConfig;

import java.util.List;

public interface FunnelRentConfigRepository {
    List<FunnelRentConfig> getAll();
}
