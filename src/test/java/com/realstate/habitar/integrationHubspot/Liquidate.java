package com.realstate.habitar.integrationHubspot;

import com.realstate.habitar.application.implementations.hubspot.HspotDealService;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "hubspot.base-url=https://api.hubapi.com",
        "hubspot.deal-app.token=[ojo-secreto-reemplazarlo*]",
        "hubspot.api.owners=/crm/v3/owners/"
})
public class Liquidate {
    @Autowired
    private HspotDealService hspotDealService;

    @Test
    void liquidate(){
        DealMasterBifurcationRecord dealMasterBifurcationRecord = new DealMasterBifurcationRecord(
                "58029834352-81663068",
                HubspotEnum.FUNNELRENT,
                "LIQUIDATE",
                StatesObjects.OPEN);

        hspotDealService.liquidateOnlyDeal(dealMasterBifurcationRecord);
    }
}
