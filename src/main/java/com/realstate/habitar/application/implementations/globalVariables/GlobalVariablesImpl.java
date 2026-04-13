package com.realstate.habitar.application.implementations.globalVariables;

import com.realstate.habitar.application.usecases.globalVariables.GlobalVariablesService;
import com.realstate.habitar.domain.dtos.globaVariables.ConfigVariablesDto;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.classes.model.GlobalVariables;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GlobalVariablesImpl implements GlobalVariablesService {


    private final DaoCrudPort<GlobalVariables> globalVariablesDaoCrudPort;

    public GlobalVariablesImpl(DaoCrudPort<GlobalVariables> globalVariablesDaoCrudPort) {
        this.globalVariablesDaoCrudPort = globalVariablesDaoCrudPort;
    }

    @Override
    public List<ConfigVariablesDto> getListGlobalVariables() {
        return globalVariablesDaoCrudPort.selectAll()
                .stream()
                .map(this::convertTo)
                .toList();
    }

    @Override
    public ConfigVariablesDto convertTo(GlobalVariables globalVariables) {
        return new ConfigVariablesDto(globalVariables.getKeyVariable(),globalVariables.getValue());
    }
}
