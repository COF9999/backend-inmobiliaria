package com.realstate.habitar.infraestructure.rest.model;


import com.realstate.habitar.application.usecases.processDeal.ProcessDealService;
import com.realstate.habitar.domain.dtos.processDeal.ProcessDealResponseDto;
import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/process-deal")
public class ProcessDealRest {

    private final ProcessDealService processDealService;

    public ProcessDealRest(ProcessDealService processDealService){
        this.processDealService = processDealService;
    }

    @GetMapping("/list")
    public List<ProcessDealResponseDto> getProcessDeals(){
        return processDealService.getProcessDeals();
    }
}
