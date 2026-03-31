package com.realstate.habitar.infraestructure.rest.dealRegister;

import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotDealKeyRecord;
import com.realstate.habitar.domain.enums.StatesObjects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal-register")
public class DealRegisterRest {

    private final DealRegistryService dealRegistryService;

    public DealRegisterRest(DealRegistryService dealRegistryService) {
        this.dealRegistryService = dealRegistryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<DealRegisterDto>> selectDealsRegister(
            @RequestParam(name = "s",required = false)
            StatesObjects stateObject){
        return ResponseEntity.ok(dealRegistryService.selectDealsRegister(stateObject.getState()));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> cancelSelectDeal(HubSpotDealKeyRecord hubSpotDealKeyRecord){
            return ResponseEntity.ok().build();
    }
}
