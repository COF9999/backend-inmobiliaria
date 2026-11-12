package com.realstate.habitar.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realstate.habitar.application.implementations.commands.HubSpotDealRegistry;
import com.realstate.habitar.application.implementations.hubspot.HspotDealService;
import com.realstate.habitar.domain.dtos.funnelsDtos.ResultDto;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.adapters.implementations.HubspotDealClient;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:file:./target/h2/evidencia;MODE=MySQL;NON_KEYWORDS=USER",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "app.db.init.enabled=true",
        "config.keyowneraccount=account_owner",
        "config.percentagesale=percentage_sale",
        "config.listing.agent=listing_property",
        "config.duration.contract=contract_duration",
        "config.total.commissions.sixmonths=total_commissions_six_months",
        "config.total.commissions.oneyear=total_commissions_one_year",
        "config.commissions.ownersixmonths=commissions_owner_six_months",
        "config.commissions.owneroneyear=commissions_owner_one_year"
})
public class AppTest {

    @Autowired
    private HspotDealService hspotDealService;

    @Autowired
    private HubSpotDealRegistry hubSpotDealRegistry;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @MockitoBean
    private HubspotDealClient hubspotDealClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(AppTest.class);

    @Test
    void stepsLiquidate() throws Exception {
        log.info("=== VERIFICANDO DATOS DEL LOADER EN H2 ===");

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM global_variable", Integer.class);
        log.info("Registros cargados por el loader: {}", count);

        ClassPathResource resource = new ClassPathResource("deals.json");
        List<HubspotDealDtoApp> savedList = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}
        );

        for (HubspotDealDtoApp rawDeal : savedList) {

            HubspotDealDtoApp currentDeal = normalizeDealPipeline(rawDeal);
            DealMasterBifurcationRecord simulatedRequest = simulateFrontendLogic(currentDeal);

            // Intercept HubSpot API call (serves both pre-liquidate and liquidate)
            when(hubspotDealClient.findDealByPrincipalInfo(any(), any()))
                    .thenReturn(currentDeal);

            // 2. STEP 1 (Pre-liquidate): Registers the deal in H2 database automatically
            hubSpotDealRegistry.manipulateCreationDealRegistry(simulatedRequest);

            // 3. STEP 2 (Liquidate): Finds the registered deal in H2 and processes the switch
            List<ResultDto> result = hspotDealService.liquidateOnlyDeal(simulatedRequest);

            // 4. Verify batch item succeeded
            assertFalse(result.isEmpty());
            assertEquals("PROCESSED", result.getFirst().message());


            log.info("\n================ REPORTE DE EVIDENCIA EN H2 ================");
            log.info("--- 1. DEAL_REGISTRY ---");
            jdbcTemplate.queryForList("SELECT id, deal_user_id, pipeline_type, amount, state FROM deal_registry")
                    .forEach(row -> log.info("{}", row));

            log.info("--- 2. DEAL_LIQUIDATE ---");
            jdbcTemplate.queryForList("SELECT * FROM deal_liquidate")
                    .forEach(row -> log.info("{}", row));

            log.info("--- 3. USER_LIQUIDATION ---");
            jdbcTemplate.queryForList("SELECT * FROM user_liquidation WHERE total_amount > 0")
                    .forEach(row -> log.info("{}", row));
        }

    }

    private HubspotDealDtoApp normalizeDealPipeline(HubspotDealDtoApp deal) {
        String formattedPipeline = switch (deal.pipelineType().toUpperCase()) {
            case "FUNNEL_SALES", "FUNNELSALES" -> "Funnel Sales";
            case "FUNNEL_RENT", "FUNNELRENT" -> "Funnel Rent";
            case "FUNNEL_RENT_PLACEMENT", "FUNNELRENTPLACEMENT" -> "Funnel Rent Placement";
            default -> deal.pipelineType();
        };

        return new HubspotDealDtoApp(
                deal.id(),
                deal.ownerId(),
                deal.nameUser() != null ? deal.nameUser() : "Test User",
                formattedPipeline,
                deal.properties()
        );
    }


    private DealMasterBifurcationRecord simulateFrontendLogic(HubspotDealDtoApp deal) {
        String pipeline = deal.pipelineType();

        String typeMethod = ("Funnel Sales".equalsIgnoreCase(pipeline) || "FUNNEL_SALES".equalsIgnoreCase(pipeline))
                ? "LIQUIDATE"
                : "PLAN";

        String hubspotEnum = pipeline.replace(" ", "").replace("_", "").toUpperCase();

        String dealMixHubId = deal.id() + "-" + deal.ownerId();

        return new DealMasterBifurcationRecord(
                dealMixHubId,
                HubspotEnum.valueOf(hubspotEnum),
                typeMethod,
                StatesObjects.OPEN
        );
    }
}
