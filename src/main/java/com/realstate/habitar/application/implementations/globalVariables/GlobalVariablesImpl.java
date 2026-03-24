package com.realstate.habitar.application.implementations.globalVariables;

import com.realstate.habitar.application.usecases.globalVariables.GlobalVariablesService;
import com.realstate.habitar.domain.dtos.globaVariables.GlobalVariablesDto;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.globalVariables.GlobalVariablesRepository;
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
    public List<GlobalVariablesDto> getListGlobalVariables() {
        return globalVariablesDaoCrudPort.selectAll()
                .stream()
                .map(this::convertTo)
                .toList();
    }

    @Override
    public GlobalVariablesDto convertTo(GlobalVariables globalVariables) {
        return new GlobalVariablesDto(globalVariables.getKeyVariable(),globalVariables.getValue());
    }
}
