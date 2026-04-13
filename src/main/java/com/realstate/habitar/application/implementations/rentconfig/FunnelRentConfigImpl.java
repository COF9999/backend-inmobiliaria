package com.realstate.habitar.application.implementations.rentconfig;

import com.realstate.habitar.application.usecases.rentconfig.FunnelRentConfigService;
import com.realstate.habitar.domain.dtos.globaVariables.ConfigVariablesDto;
import com.realstate.habitar.infraestructure.adapters.interfaces.funnelrentconfig.FunnelRentConfigRepository;
import com.realstate.habitar.infraestructure.classes.model.FunnelRentConfig;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FunnelRentConfigImpl implements FunnelRentConfigService {

    private final FunnelRentConfigRepository funnelRentConfigRepository;

    public FunnelRentConfigImpl(FunnelRentConfigRepository funnelRentConfigRepository) {
        this.funnelRentConfigRepository = funnelRentConfigRepository;
    }

    @Override
    public Map<String,String> getListGlobalVariables() {
        return funnelRentConfigRepository.getAll().stream()
                .map(this::convertTo)
                .collect(Collectors.toMap(ConfigVariablesDto::key,ConfigVariablesDto::value))
                ;
    }

    @Override
    public ConfigVariablesDto convertTo(FunnelRentConfig funnelRentConfig) {
        return new ConfigVariablesDto(funnelRentConfig.getKeyVariable(),funnelRentConfig.getValueVariable());
    }
}
