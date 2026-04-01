package com.realstate.habitar.infraestructure.rest.hubspot;

import com.realstate.habitar.application.implementations.commands.HubSpotDealRegistry;
import com.realstate.habitar.application.implementations.hubspot.HspotDealService;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController()
@RequestMapping("/deal")
public class HubSpotDealRest {

    private final HspotDealService hspotDealEventService;

    private final HubSpotDealRegistry hubSpotDealRegistry;

    public HubSpotDealRest(HspotDealService hspotDealEventService,HubSpotDealRegistry hubSpotDealRegistry) {
        this.hspotDealEventService = hspotDealEventService;
        this.hubSpotDealRegistry = hubSpotDealRegistry;
    }

    @PostMapping("/register-deal")
    public ResponseEntity<Void> registerSelectDeal(@RequestBody DealMasterBifurcationRecord hubSpotDealKeyRecord)  {
        hubSpotDealRegistry.deciteTocreateDealRegistry(hubSpotDealKeyRecord);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deals-not-register")
    public ResponseEntity<List<HubspotDealDtoApp>> getDealsNotRegister(@RequestBody LiquidationTimeRecord liquidationTimeRecord){
        System.out.println("DEVUELTA : "+liquidationTimeRecord.toString());
        return ResponseEntity.ok(hspotDealEventService.getDealsHubspot(liquidationTimeRecord));
    }

    @PostMapping("/liquidate-select-deal")
    public ResponseEntity<Void> liquidateSelectDeal(@RequestBody DealMasterBifurcationRecord hubSpotDealKeyRecord)  {
        hspotDealEventService.liquidateOnlyDeal(hubSpotDealKeyRecord);
        System.out.println("---------------------------------BIEN");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/report-date")
    public ResponseEntity<Void> liquidateByMonth(@RequestBody LiquidationTimeRecord liquidationMonthRecord){
        try {
            hspotDealEventService.liquidationReportByMonth(liquidationMonthRecord);
        }catch (Exception ex){
            log.error("Error procesando evento HubSpot: {}", ex);
        }

        return ResponseEntity.ok().build();
    }






}

