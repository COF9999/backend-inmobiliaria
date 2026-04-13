package com.realstate.habitar.infraestructure.rest.dealRegister;

import com.realstate.habitar.application.implementations.commands.HubSpotDealRegistry;
import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.response.ResponseDto;
import com.realstate.habitar.domain.enums.StatesObjects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal-register")
public class DealRegisterRest {

    private final HubSpotDealRegistry hubSpotDealRegistry;
    private final DealRegistryService dealRegistryService;

    public DealRegisterRest(HubSpotDealRegistry hubSpotDealRegistry, DealRegistryService dealRegistryService) {
        this.hubSpotDealRegistry = hubSpotDealRegistry;
        this.dealRegistryService = dealRegistryService;
    }


    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createDealRegistry(@RequestBody DealMasterBifurcationRecord dealMasterBifurcationRecord){
        System.out.println("-----------------------------------a------------------");
        hubSpotDealRegistry.manipulateCreationDealRegistry(dealMasterBifurcationRecord);
         return ResponseEntity.ok(new ResponseDto("ok"));
    }

    @GetMapping("/")
    public ResponseEntity<List<DealRegisterDto>> selectDealsRegister(
            @RequestParam(name = "s")
            StatesObjects stateObject){
        return ResponseEntity.ok(dealRegistryService.selectDealsRegister(stateObject.getState()));
    }



    @PatchMapping("/update")
    public ResponseEntity<Void> cancelSelectDeal(DealMasterBifurcationRecord hubSpotDealKeyRecord){
            return ResponseEntity.ok().build();
    }
}
