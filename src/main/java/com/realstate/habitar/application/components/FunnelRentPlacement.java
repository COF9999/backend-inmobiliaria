package com.realstate.habitar.application.components;

import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dispactchers.TypeMethodFunnel;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class FunnelRentPlacement implements PipelineHandler {
    @Override
    public PipelineType pipelineKey() {
        return PipelineType.FUNNELRENTPLACEMENT;
    }

    @Override
    public void handle(Map<String, List<HubspotDealDtoApp>> listDeals, TypeMethodFunnel typeMethodFunnel) {
        //handleRentPlacement(dealsByOwner);
    }



    private void handleRentPlacement(Map<String,List<HubspotDealDtoApp>> listDeals) {
        System.out.println("FUNNELRENT-> ");
        BigDecimal bigDecimal = new BigDecimal("134");
        String owner = "";
        Double totalAmount = 0.0;
        for (Map.Entry<String,List<HubspotDealDtoApp>> dealByOwner:listDeals.entrySet()){
            owner=  dealByOwner.getKey();
            for (HubspotDealDtoApp hubspotDealDtoApp:dealByOwner.getValue()){
                totalAmount += Double.parseDouble(hubspotDealDtoApp.properties().get("amount"));
            }
        }

        System.out.println();
        System.out.println(totalAmount);
    }
}
