package com.realstate.habitar.application.components;

import com.realstate.habitar.application.usecases.components.SalesComponentInterface;
import com.realstate.habitar.application.usecases.hubspot.HubspotCommand;
import com.realstate.habitar.application.usecases.user.UserService;
import com.realstate.habitar.domain.FunnelSales;
import com.realstate.habitar.domain.PipelineHandler;
import com.realstate.habitar.domain.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.CalculateOwnerLiquidationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationMonthReportRecord;
import com.realstate.habitar.domain.dtos.sales.SalesToAppRecord;
import com.realstate.habitar.domain.dtos.sales.SalesToDomainRecord;
import com.realstate.habitar.domain.dtos.user.UserRequestDto;
import com.realstate.habitar.domain.ports.user.UserDaoPort;
import com.realstate.habitar.domain.rules.ExportRules;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.global.infraestructure.models.Role;
import com.realstate.habitar.global.infraestructure.models.User;
import com.realstate.habitar.infraestructure.adapters.interfaces.SalesCommissionScaleRepository;
import com.realstate.habitar.infraestructure.adapters.interfaces.UserLiquidationRepository;
import com.realstate.habitar.infraestructure.config.data.GlobalVariablesCache;
import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Component
public class FunnelSalesComponent implements PipelineHandler, SalesComponentInterface {

    @Value("${config.keyowneraccount}")
    private String keyOwnerAccount;

    @Value("${config.percentagesale}")
    private String percentageSale;

    @Value("${config.export.path}")
    private String pathToExport;


    private final SalesCommissionScaleRepository salesCommissionScaleRepository;

    private final FunnelSales funnelSales;

    private final UserLiquidationRepository userLiquidationRepository;

    private final DaoCrudPort<ProcessedDeal> processedDealDaoCrudPort;

    private final GlobalVariablesCache globalVariablesCache;

    private final UserDaoPort userUserDaoPort;

    private final HubspotCommand hubspotCommands;

    private final UserService userService;

    public FunnelSalesComponent(
                                SalesCommissionScaleRepository salesCommissionScaleRepository,
                                FunnelSales funnelSales,
                                UserLiquidationRepository userLiquidationRepository,
                                DaoCrudPort<ProcessedDeal> processedDealDaoCrudPort,
                                GlobalVariablesCache globalVariablesCache,
                                UserDaoPort userUserDaoPort,
                                HubspotCommand hubspotCommands,
                                UserService userService
                               ){
        this.salesCommissionScaleRepository = salesCommissionScaleRepository;
        this.funnelSales = funnelSales;
        this.userLiquidationRepository = userLiquidationRepository;
        this.processedDealDaoCrudPort = processedDealDaoCrudPort;
        this.globalVariablesCache = globalVariablesCache;
        this.userUserDaoPort = userUserDaoPort;
        this.hubspotCommands = hubspotCommands;
        this.userService = userService;
    }

    public PipelineType pipelineKey() {
        return PipelineType.FUNNELSALES;
    }

    @Override
    public void handle(Map<String, List<HubspotDealDtoApp>> listDeals) {
        try {
            CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord = calculateOwnerLiquidation(listDeals);
            updateAmount(calculateOwnerLiquidationRecord.resultOperation());
            closeDealsAlreadyProccessed(calculateOwnerLiquidationRecord.dealsProccesed());
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void generateReport(Map<String, List<HubspotDealDtoApp>> listDeals) {
        try {
            CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord = calculateOwnerLiquidation(listDeals);
            List<LiquidationMonthReportRecord> monthReportRecordList = new ArrayList<>();
            for (Map.Entry<String,BigDecimal> mapOwnerLiquidation:calculateOwnerLiquidationRecord.resultOperation().entrySet()){
                Optional<User> userFound = userUserDaoPort.getUser(mapOwnerLiquidation.getKey());

                System.out.println(mapOwnerLiquidation.getKey()+" %%%%% "+mapOwnerLiquidation.getValue());

               if (userFound.isPresent()){
                   User user = userFound.get();
                   monthReportRecordList.add(new LiquidationMonthReportRecord(user.getName(),user.getHubId(),mapOwnerLiquidation.getValue()));
               }else {
                   monthReportRecordList.add(new LiquidationMonthReportRecord("Anonimo",mapOwnerLiquidation.getKey(),mapOwnerLiquidation.getValue()));
               }
            }
            ExportRules.exportLiquidationReport(monthReportRecordList,pathToExport);
        }catch (Exception e){
            throw new IllegalArgumentException("Problem "+e.getMessage());
        }
    }

    private CalculateOwnerLiquidationRecord calculateOwnerLiquidation(Map<String,List<HubspotDealDtoApp>> listDeals) {
        List<SalesCommissionScale> listSalesCommissionScales = getAllSalesScales();
        Set<HubspotDealDtoApp> dealsProccesed = new HashSet<>();
        String valueOwnerAccount = globalVariablesCache.get(keyOwnerAccount);
        BigDecimal valuePercentageSale = new BigDecimal(globalVariablesCache.get(percentageSale));
        System.out.println("FUNNELSALES-> ");
        BigDecimal acumAmountAgent = BigDecimal.ZERO;
        BigDecimal acumAmountCompany = BigDecimal.ZERO;
        String owner = "";
        Map<String,BigDecimal> resultOperation = new HashMap<>();
        try {
            for (Map.Entry<String, List<HubspotDealDtoApp>> dealByOwner : listDeals.entrySet()) {
                owner = dealByOwner.getKey();
                for (HubspotDealDtoApp hubspotDealDtoApp : dealByOwner.getValue()) {
                    if (hubspotDealDtoApp.ownerId().equalsIgnoreCase(valueOwnerAccount)) {
                        SalesToAppRecord salesToAppRecord = funnelSales.operate(new SalesToDomainRecord(hubspotDealDtoApp, listSalesCommissionScales, valuePercentageSale, true));
                        acumAmountCompany = acumAmountCompany.add(salesToAppRecord.acumAmountCompany());
                    } else {
                        SalesToAppRecord salesToAppRecord = funnelSales.operate(new SalesToDomainRecord(hubspotDealDtoApp, listSalesCommissionScales, valuePercentageSale, false));
                        acumAmountAgent = acumAmountAgent.add(salesToAppRecord.acumAmountAgent());
                        acumAmountCompany = acumAmountCompany.add(salesToAppRecord.acumAmountCompany());
                        resultOperation.put(owner, acumAmountAgent);
                    }
                    dealsProccesed.add(hubspotDealDtoApp);
                }
                acumAmountAgent = BigDecimal.ZERO;
            }
            resultOperation.put(valueOwnerAccount, acumAmountCompany);
            return new CalculateOwnerLiquidationRecord(resultOperation,dealsProccesed);
        }catch (Exception e){
            throw e;
        }
    }

    private void closeDealsAlreadyProccessed(Set<HubspotDealDtoApp> dealsProccesed) {
        for (HubspotDealDtoApp deal:dealsProccesed){
            Long idUserLiquidator = userLiquidationRepository.getIdUserLiquidation(deal.ownerId());
            ProcessedDeal processedDeal = new ProcessedDeal(
                    deal.id()+"-"+deal.ownerId(),
                    deal.pipelineType(),
                    idUserLiquidator,
                    new BigDecimal(deal.properties().get("amount")),
                    OffsetDateTime.parse(deal.properties().get("closedate")).toLocalDateTime(),
                    LocalDateTime.now());
            processedDealDaoCrudPort.create(processedDeal);
        }
    }

    private void updateAmount( Map<String,BigDecimal> resultOperation) {
        String hubId = "";
        BigDecimal acum = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> map : resultOperation.entrySet()) {
            hubId = map.getKey();
            acum  = map.getValue();
            if (userLiquidationRepository.isExistUserLiquidation(hubId)) {
                userLiquidationRepository.addAmount(hubId, acum);
            } else {
                createUserIfExists(hubId);
                userLiquidationRepository.addAmount(hubId,acum);
            }
        }
    }

    private void createUserIfExists(String hubId) {
       // Map<String, Object> activeOwner = hubspotCommands.findOwnerByHubId(hubId);

        Map<String, Object> activeOwner = new HashMap<>();

        if (activeOwner != null) {
            // todo crear usuario
            boolean isActive = !((boolean) activeOwner.get("archived"));
            String firstName = (String) activeOwner.get("firstName");
            String username = firstName;
            String name = firstName + " "+ ((String) activeOwner.get("lastName"));
            String email = (String) activeOwner.get("email");
            String identification = "123GENERIC";
            Set<Role> setRoles = Set.of(new Role(null, "ROLE_USER"));
            userService.create(new UserRequestDto(
                    null,
                    hubId,
                    isActive,
                    setRoles,
                    name,
                    username,
                    email,
                    identification,
                    "",
                    false
            ));
        }
    }

    private List<SalesCommissionScale> getAllSalesScales() {
        try {
            return salesCommissionScaleRepository.getAllCommisionsSalesScale();
        }catch (Exception e){
         // todo   throw new RuntimeException("ERROR EN LA ESCALA");
            throw new RuntimeException("ERROR EN LA ESCALA");
        }
    }

}
