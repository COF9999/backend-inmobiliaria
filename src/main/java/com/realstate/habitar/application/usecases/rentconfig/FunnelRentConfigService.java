package com.realstate.habitar.application.usecases.rentconfig;

import com.realstate.habitar.domain.dtos.globaVariables.ConfigVariablesDto;
import com.realstate.habitar.infraestructure.classes.model.FunnelRentConfig;
import java.util.List;
import java.util.Map;

public interface FunnelRentConfigService {
    Map<String,String> getListGlobalVariables();

    ConfigVariablesDto convertTo(FunnelRentConfig funnelRentConfig);
}
