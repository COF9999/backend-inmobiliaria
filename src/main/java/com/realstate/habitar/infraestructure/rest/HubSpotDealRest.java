package com.realstate.habitar.infraestructure.rest;

import com.realstate.habitar.application.implementations.hubspot.HspotDealService;
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

    public HubSpotDealRest(HspotDealService hspotDealEventService) {
        this.hspotDealEventService = hspotDealEventService;
    }


    @PostMapping("/liquidate-select-deals")
    public ResponseEntity<Void> liquidateSelectDeals(@RequestBody List<HubspotDealDtoApp> listDeals)  {
        hspotDealEventService.liquidateSelectDeals(listDeals);
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


    @PostMapping("/test")
    public ResponseEntity<Void> dealReciever(@RequestBody List<HubspotDealDtoApp> listDeals)  {
        try {
            hspotDealEventService.processDealTest(listDeals);
        }catch (Exception ex){
            log.error("Error procesando evento HubSpot: {}", ex);
        }

        return ResponseEntity.ok().build();
    }



}

