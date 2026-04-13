package com.realstate.habitar.infraestructure.rest.dealLiquidate;

import com.realstate.habitar.application.usecases.dealLiquidate.DealLiquidateService;
import com.realstate.habitar.domain.dtos.dealLiquidate.DealLiquidateResponseDto;
import com.realstate.habitar.domain.enums.StatesObjects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal-liquidate")
public class DealLiquidateRest {

    private final DealLiquidateService dealLiquidateService;

    public DealLiquidateRest(DealLiquidateService dealLiquidateService) {
        this.dealLiquidateService = dealLiquidateService;
    }

    @GetMapping("/")
    public ResponseEntity<List<DealLiquidateResponseDto>> getDealsByStatus(
            @RequestParam(name = "s") StatesObjects statesObjects
            ){
        System.out.println("entoooooo");
        return ResponseEntity.ok(dealLiquidateService.getDealLiquidateByStatus(statesObjects));
    }
}
