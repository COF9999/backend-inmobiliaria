package com.realstate.habitar.application.usecases.globalVariables;

import com.realstate.habitar.domain.dtos.globaVariables.ConfigVariablesDto;
import com.realstate.habitar.infraestructure.classes.model.GlobalVariables;
import java.util.List;

public interface GlobalVariablesService {
    List<ConfigVariablesDto> getListGlobalVariables();

    ConfigVariablesDto convertTo(GlobalVariables globalVariables);
}
