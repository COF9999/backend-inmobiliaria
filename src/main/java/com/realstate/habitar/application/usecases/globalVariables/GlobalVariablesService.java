package com.realstate.habitar.application.usecases.globalVariables;

import com.realstate.habitar.domain.dtos.globaVariables.GlobalVariablesDto;
import com.realstate.habitar.infraestructure.classes.model.GlobalVariables;

import java.util.List;

public interface GlobalVariablesService {
    List<GlobalVariablesDto> getListGlobalVariables();

    GlobalVariablesDto convertTo(GlobalVariables globalVariables);
}
