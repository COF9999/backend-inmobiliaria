package com.realstate.habitar.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realstate.habitar.application.components.FunnelRentComponent;
import com.realstate.habitar.application.implementations.commands.HubSpotDealRegistry;
import com.realstate.habitar.application.implementations.hubspot.HspotDealService;
import com.realstate.habitar.domain.dtos.funnelsDtos.ResultDto;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.adapters.implementations.HubspotDealClient;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceAlreadyExists;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Autowired
    private FunnelRentComponent funnelRentComponent;

    @Test
    @Order(1)
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

    @Test
    @Order(2)
    void payOneMonthOfRent() {
        // 1. take the last paymentPlan that isn't realized for extract the UUID
        String uuid = jdbcTemplate.queryForObject(
                "SELECT uuid FROM payment_plan WHERE is_pay = false LIMIT 1", String.class);

        // 2. Pay the Month
        funnelRentComponent.liquidatePlanDeal(uuid);

        // 3. the plan has stayed as market
        Boolean isPay = jdbcTemplate.queryForObject(
                "SELECT is_pay FROM payment_plan WHERE uuid = ?", Boolean.class, uuid);
        assertTrue(isPay);

        // 4. Check if the register was realized
        Integer payments = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment_realized", Integer.class);
        assertEquals(1, payments);

        // 5.Pay the month again will produce a error
        assertThrows(ResourceAlreadyExists.class,
                () -> funnelRentComponent.liquidatePlanDeal(uuid));

        log.info("--- PAYMENT_PLAN pagado ---");
        jdbcTemplate.queryForList("SELECT * FROM payment_plan WHERE uuid = ?", uuid)
                .forEach(row -> log.info("{}", row));
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
