package com.realstate.habitar.infraestructure.rest.model;

import com.realstate.habitar.application.usecases.globalVariables.GlobalVariablesService;
import com.realstate.habitar.domain.dtos.globaVariables.GlobalVariablesDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/g-variables")
public class GlobalVariablesRest {

    private final GlobalVariablesService globalVariablesService;

    public GlobalVariablesRest(GlobalVariablesService globalVariablesService) {
        this.globalVariablesService = globalVariablesService;
    }

    @GetMapping("/")
    public List<GlobalVariablesDto> getAllVariables(){
        return globalVariablesService.getListGlobalVariables();
    }
}
