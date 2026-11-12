package com.realstate.habitar.application.components;

import com.realstate.habitar.application.components.helpers.sales.SalesHelper;
import com.realstate.habitar.application.implementations.rentconfig.FunnelRentConfigImpl;
import com.realstate.habitar.application.usecases.dealLiquidate.DealLiquidateService;
import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.application.usecases.paymentRealized.PaymentRealizedService;
import com.realstate.habitar.application.usecases.paymentplan.PaymentPlanService;
import com.realstate.habitar.application.usecases.user.UserServiceBasicOperations;
import com.realstate.habitar.domain.baselogic.FunnelRent;
import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.funnelsDtos.ResultDto;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;
import com.realstate.habitar.domain.dtos.hubspot.CalculateOwnerLiquidationRecord;
import com.realstate.habitar.domain.dtos.hubspot.DealMasterBifurcationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.FunnelRentEnum;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.domain.rules.CronoRules;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceAlreadyExists;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceNotFound;
import com.realstate.habitar.infraestructure.classes.model.DealLiquidate;
import com.realstate.habitar.infraestructure.classes.model.PaymentPlan;
import com.realstate.habitar.infraestructure.classes.model.PaymentRealized;
import com.realstate.habitar.infraestructure.classes.model.User;
import com.realstate.habitar.infraestructure.config.data.GlobalVariablesCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class FunnelRentComponent implements PipelineHandler {

    @Value("${config.keyowneraccount}")
    private String keyOwnerAccount;

    @Value("${config.listing.agent}")
    private String keyListingAgent;

    @Value("${config.duration.contract}")
    private String keyDurationContract;

    @Value("${config.total.commissions.oneyear}")
    private String keyTotalCommissionsOneYear;

    @Value("${config.total.commissions.sixmonths}")
    private String keyTotalCommissionsSixMonths;

    @Value("${config.commissions.owneroneyear}")
    private String keyCommissionsOwnerOneYear;

    @Value("${config.commissions.ownersixmonths}")
    private String keyCommissionsOwnerSixMonths;

    private final FunnelRentConfigImpl funnelRentConfigImpl;

    private final GlobalVariablesCache globalVariablesCache;

    private final DealLiquidateService dealLiquidateService;

    private final DealRegistryService dealRegistryService;

    private final UserServiceBasicOperations userServiceBasicOperations;

    private final PaymentPlanService paymentPlanService;

    private final PaymentRealizedService paymentRealizedService;

    private final SalesHelper salesHelper;


    private static final Logger log = LoggerFactory.getLogger(FunnelRentComponent.class);


    public FunnelRentComponent(FunnelRentConfigImpl funnelRentConfigImpl, GlobalVariablesCache globalVariablesCache, DealLiquidateService dealLiquidateService, DealRegistryService dealRegistryService, UserServiceBasicOperations userServiceBasicOperations, PaymentPlanService paymentPlanService, PaymentRealizedService paymentRealizedService, SalesHelper salesHelper) {
        this.funnelRentConfigImpl = funnelRentConfigImpl;
        this.globalVariablesCache = globalVariablesCache;
        this.dealLiquidateService = dealLiquidateService;
        this.dealRegistryService = dealRegistryService;
        this.userServiceBasicOperations = userServiceBasicOperations;
        this.paymentPlanService = paymentPlanService;
        this.paymentRealizedService = paymentRealizedService;
        this.salesHelper = salesHelper;
    }

    @Override
    public PipelineType pipelineKey() {
        return PipelineType.FUNNEL_RENT;
    }

    @Override
    public ResultDto handle(String candidateMixId,Map<String, List<HubspotDealDtoApp>> listDeals, String typeMethodFunnel) {
        return switch (typeMethodFunnel){
            case "PLAN" -> {
                generatePlanDeals(candidateMixId,listDeals);
                yield new ResultDto("PROCESSED",null);
            }
            default -> new ResultDto("NOTMATCH",null);
        };
    }

    private BigDecimal commissionRent30(BigDecimal amountDeal){
        BigDecimal percentage = new BigDecimal("0.10"); // 10%
        return amountDeal.multiply(percentage).setScale(0, RoundingMode.DOWN);

    }

    @Transactional
    public void liquidatePlanDeal(String uuid){
          PaymentPlan paymentPlan = paymentPlanService.findByUuid(uuid);
          if (paymentPlan.isPay()){
              System.out.println("Ya esta pago");
              throw new ResourceAlreadyExists("Ya esta pago");
          }
          User user = paymentPlan.getUser();
          BigDecimal amountDeal = paymentPlan.getDealLiquidate().getDealRegistry().getAmount();
          BigDecimal commission30calculated = commissionRent30(amountDeal);
          paymentPlan.setPay(true);
          paymentRealizedService.createPaymentRealized(new PaymentRealized(null,paymentPlan,CronoRules.currentLocalDatime()));
          salesHelper.visit(new Resume(user.getName(),user.getHubId(),commission30calculated,0));
    }

    private void generatePlanDeals(String candidateMixId,Map<String, List<HubspotDealDtoApp>> listDeals){
        try {
            calculateRoadPlan(candidateMixId,listDeals);
        }catch (Exception e){
            throw e;
        }
    }

    private void calculateRoadPlan(String candidateMixId,Map<String,List<HubspotDealDtoApp>> listDeals) {
        Map<String,String> configMapRent = funnelRentConfigImpl.getListGlobalVariables();
        String valueOwnerAccount = globalVariablesCache.get(keyOwnerAccount);
        System.out.println("FUNNELRENT-> ");
        String listingAgentHubId = "";
        User userCompany = userServiceBasicOperations.findUserByHubId(valueOwnerAccount).orElseThrow();
        try {
            for (Map.Entry<String, List<HubspotDealDtoApp>> dealByOwner : listDeals.entrySet()) {
                for (HubspotDealDtoApp hubspotDealDtoApp : dealByOwner.getValue()) {
                    listingAgentHubId = hubspotDealDtoApp.properties().get(keyListingAgent);
                    if (listingAgentHubId==null) {
                        Function<FunnelRent.FunnelRentDecitions,Void> supplierSixMonths = (funnelRentDecitions)->{
                            executeLogicSameAgent(keyTotalCommissionsSixMonths,hubspotDealDtoApp,userCompany,funnelRentDecitions);
                            return null;
                        };
                        Function<FunnelRent.FunnelRentDecitions,Void> supplierOneYear = (funnelRentDecitions)->{
                            executeLogicSameAgent(keyTotalCommissionsOneYear,hubspotDealDtoApp,userCompany,funnelRentDecitions);
                            return null;
                        };
                        rentRules(candidateMixId,hubspotDealDtoApp,configMapRent,supplierSixMonths,supplierOneYear);
                    } else {
                        Function<FunnelRent.FunnelRentDecitions,Void> supplierSixMonths = (funnelRentDecitions)->{
                            executeLogicDifferentAgent(keyCommissionsOwnerSixMonths,hubspotDealDtoApp,userCompany,funnelRentDecitions);
                            return null;
                        };
                        Function<FunnelRent.FunnelRentDecitions,Void> supplierOneYear = (funnelRentDecitions)->{
                            executeLogicDifferentAgent(keyCommissionsOwnerOneYear,hubspotDealDtoApp,userCompany,funnelRentDecitions);
                            return null;
                        };
                        rentRules(candidateMixId,hubspotDealDtoApp,configMapRent,supplierSixMonths,supplierOneYear);
                    }
                }
            }
        }catch (Exception e){
            throw e;
        }
    }

    private FunnelRentEnum.TypeUsers.InstaceSizePlanContractUsersRecord instaceSizePlanContractUsers(int durationContract,LocalDateTime baseDate){
        FunnelRent funnelRent = new FunnelRent();
        return  FunnelRentEnum.TypeUsers.createInstance(new FunnelRentEnum.TypeUsers[durationContract],false);

    }

    private void executeLogicSameAgent(String commissionKey, HubspotDealDtoApp hubspotDealDtoApp, User userCompany, FunnelRent.FunnelRentDecitions funnelRentDecitions){
        int commisionsNormalAgentNumber = Integer.parseInt(funnelRentDecitions.map().get(commissionKey));
        dealRegistryService.executeTransition(new DealMasterBifurcationRecord(funnelRentDecitions.candidateMixId(),null,null, StatesObjects.SUCCESFULLY));
        DealLiquidate dealLiquidate = delegateCreationDealLiquidation(hubspotDealDtoApp,FunnelRentEnum.WrapperUser.SAMEAGENT);
        FunnelRentEnum.TypeUsers.InstaceSizePlanContractUsersRecord instacePlanContractRecord = instaceSizePlanContractUsers(funnelRentDecitions.contractDuration(),dealLiquidate.getDealRegistry().getClosedAt());
        FunnelRentEnum.TypeUsers[] planContractUsers = instacePlanContractRecord.instance();
        Map<FunnelRentEnum.TypeUsers, FunnelRentEnum.TypeUsers.TypeAgentRecord> mapDecition = new HashMap<>();
        mapDecition.put(FunnelRentEnum.TypeUsers.NORMALAGENT, FunnelRentEnum.TypeUsers.NORMALAGENT.getTypeAgentRecord(commisionsNormalAgentNumber   ,instacePlanContractRecord.isAppointment()));
        sameOwnerAndListing(planContractUsers,mapDecition);
        delegateCreationPlan(FunnelRentEnum.WrapperUser.SAMEAGENT,planContractUsers,dealLiquidate,userCompany);
    }

    private void executeLogicDifferentAgent(String commisionKey, HubspotDealDtoApp hubspotDealDtoApp, User userCompany, FunnelRent.FunnelRentDecitions funnelRentDecitions){
        int commisionsNormalAgent = Integer.parseInt(funnelRentDecitions.map().get(commisionKey));
        int commissionsListingAgent = (Integer.parseInt(funnelRentDecitions.map().get(keyTotalCommissionsSixMonths)))-(commisionsNormalAgent);
        int commissionsNormalAgentModified = commissionsListingAgent + commisionsNormalAgent;
        dealRegistryService.executeTransition(new DealMasterBifurcationRecord(funnelRentDecitions.candidateMixId(),null,null, StatesObjects.SUCCESFULLY));
        DealLiquidate dealLiquidate = delegateCreationDealLiquidation(hubspotDealDtoApp,FunnelRentEnum.WrapperUser.DIFFERENTAGENT);
        FunnelRentEnum.TypeUsers.InstaceSizePlanContractUsersRecord instacePlanContractRecord = instaceSizePlanContractUsers(funnelRentDecitions.contractDuration(),dealLiquidate.getDealRegistry().getClosedAt());
        FunnelRentEnum.TypeUsers[] planContractUsers = instacePlanContractRecord.instance();
        Map<FunnelRentEnum.TypeUsers, FunnelRentEnum.TypeUsers.TypeAgentRecord> mapDecition = new HashMap<>();
        mapDecition.put(FunnelRentEnum.TypeUsers.LISTINGAGENT, FunnelRentEnum.TypeUsers.LISTINGAGENT.getTypeAgentRecord(commissionsListingAgent   ,instacePlanContractRecord.isAppointment()));
        mapDecition.put(FunnelRentEnum.TypeUsers.NORMALAGENT, FunnelRentEnum.TypeUsers.NORMALAGENT.getTypeAgentRecord(commissionsNormalAgentModified   ,instacePlanContractRecord.isAppointment()));
        differentOwnerAndListing(planContractUsers,mapDecition);
        delegateCreationPlan(FunnelRentEnum.WrapperUser.DIFFERENTAGENT,planContractUsers,dealLiquidate,userCompany);
    }

    private User relateUsers(FunnelRentEnum.TypeUsers[] planContract,
                             int i,
                             DealLiquidate dealLiquidate,
                             User userCompany){
        User userAdded = null;
        if (planContract[i] == FunnelRentEnum.TypeUsers.NORMALAGENT || planContract[i] == FunnelRentEnum.TypeUsers.LISTINGAGENT){
            userAdded = dealLiquidate.getListUsers().get(planContract[i].getOrder());
        }else {
            userAdded = userCompany;
        }
        return userAdded;
    }

    private DealLiquidate delegateCreationDealLiquidation(HubspotDealDtoApp hubspotDealDtoApp, FunnelRentEnum.WrapperUser wrapperUser){
        return dealLiquidateService.createDealLiquidation(hubspotDealDtoApp,
                wrapperUser.delegateFunction(hubspotDealDtoApp,
                        userServiceBasicOperations,
                        keyListingAgent)
        );
    }

    // CAmbiar a Private
    public void sameOwnerAndListing(FunnelRentEnum.TypeUsers[] typeUsers,
                                    Map<FunnelRentEnum.TypeUsers, FunnelRentEnum.TypeUsers.TypeAgentRecord> mapDecition) {
        Function<Integer,Void> functionFill = (iteration)->{
            if (FunnelRent.Apportionment.getAppointMentDecition(iteration,mapDecition.get(FunnelRentEnum.TypeUsers.NORMALAGENT))){
                typeUsers[iteration] = FunnelRentEnum.TypeUsers.NORMALAGENT;
            }
            else {
                typeUsers[iteration] = FunnelRentEnum.TypeUsers.REALESTATE;
            }
            return null;
        };
        fillRoad(typeUsers.length, functionFill);
    }

    // CAmbiar a Private
    public void differentOwnerAndListing(FunnelRentEnum.TypeUsers[] typeUsers,
                                         Map<FunnelRentEnum.TypeUsers, FunnelRentEnum.TypeUsers.TypeAgentRecord> mapDecition){

        Function<Integer,Void> functionFill = (iteration)->{
            if (FunnelRent.Apportionment.getAppointMentDecition(iteration,mapDecition.get(FunnelRentEnum.TypeUsers.LISTINGAGENT))){
                typeUsers[iteration] = FunnelRentEnum.TypeUsers.LISTINGAGENT;
            }else if (FunnelRent.Apportionment.getAppointMentDecition(iteration,mapDecition.get(FunnelRentEnum.TypeUsers.NORMALAGENT))){
                typeUsers[iteration] = FunnelRentEnum.TypeUsers.NORMALAGENT;
            }else {
                typeUsers[iteration] = FunnelRentEnum.TypeUsers.REALESTATE;
            }

            return null;
        };

        fillRoad(typeUsers.length,functionFill);
    }

    private <R> void fillRoad(int lenghtObj,
                              Function<Integer,R> function){
        for (int i = 0;i<lenghtObj;i++){
            function.apply(i);
        }
    }


    // Cambiar a Private
    public void delegateCreationPlan(FunnelRentEnum.WrapperUser wrapperUser ,FunnelRentEnum.TypeUsers[] planContract,  DealLiquidate dealLiquidate,User userCompany){
        LocalDateTime baseDate = dealLiquidate.getDealRegistry().getClosedAt();
        List<PaymentPlan> resultList = switch (wrapperUser){
            case SAMEAGENT,DIFFERENTAGENT -> relatePaymentPlan(planContract,dealLiquidate,userCompany,baseDate);
        };

        resultList.forEach(p -> {
            System.out.println("------------------------------------------");
            System.out.println("Plan ID: " + p.getId());
            System.out.println("Deal ID: " + (p.getDealLiquidate() != null ? p.getDealLiquidate().toString() : "N/A"));
            System.out.println("Fechas: " + p.getTraceFirstDate() + " a " + p.getTraceSecondDate());
            System.out.println("Usuario: " + (p.getUser() != null ? p.getUser().getId() : "Sin usuario"));
            System.out.println("RestDays: " + (p.getRestDays() != null ? p.getRestDays() : -1));
        });

        paymentPlanService.batchPaymentPlan(resultList);
    }

    public List<PaymentPlan> relatePaymentPlan(FunnelRentEnum.TypeUsers[] planContract, DealLiquidate dealLiquidate, User userCompany, LocalDateTime baseDate){
        LocalDateTime finalDateContract = baseDate.plusYears(1);
        LocalDateTime startCurrentDate;
        LocalDateTime endCurrentDate;
        LocalDateTime consecutiveMonth = null;
        long restDays = 0;
        User userAdded;
        PaymentPlan paymentPlan = null;
        List<PaymentPlan> paymentPlanList = new ArrayList<>();
        for (int i=0;i<planContract.length;i++){
            consecutiveMonth = (i == 0) ? baseDate.plusMonths(1): consecutiveMonth.plusMonths(1);
            endCurrentDate = (i==planContract.length -1) ? finalDateContract :CronoRules.adjustTo30(consecutiveMonth);
            userAdded = relateUsers(planContract,i,dealLiquidate,userCompany);
            paymentPlan = PaymentPlan.builder()
                    .dealLiquidate(dealLiquidate)
                    .traceSecondDate(endCurrentDate)
                    .user(userAdded)
                    .restDays(0L)
                    .build();
            paymentPlanList.add(paymentPlan);
        }
        return paymentPlanList;
    }

    private <T,W,R> void rentRules(
            String candidateMixId,
            HubspotDealDtoApp hubspotDealDtoApp,
            Map<String,String> configMapRent,
            Function<FunnelRent.FunnelRentDecitions,R> biFunctionSixMonths,
            Function<FunnelRent.FunnelRentDecitions,R> biFunctionOneYear
    ){

        log.info("\n================ " +
                "CLAVE ENCONTRADA ???? ================"
                );
        log.info("--- 1. PROPERTIES ---  "+ keyDurationContract);
        log.info("--- 2. KEY DURATION ---  "+ configMapRent.get(keyDurationContract));

        String durationContractFromHubspot = hubspotDealDtoApp.properties().get(configMapRent.get(keyDurationContract));
        int durationContract = HubspotEnum.PropertiesHubspot.Rent.CONTRACTDURATION.extractNumber(durationContractFromHubspot);
        if (durationContract==6){
            int numberCommisionsSixMonths = Integer.parseInt(configMapRent.get(keyTotalCommissionsSixMonths));
            if (numberCommisionsSixMonths<=6){
                biFunctionSixMonths.apply(FunnelRent.instanceFunnelRentDecitions(candidateMixId,durationContract,configMapRent));
            }
        }else if (durationContract==12){
            int numberCommisionsOneYear = Integer.parseInt(configMapRent.get(keyTotalCommissionsOneYear));
            if (numberCommisionsOneYear<=12){
                biFunctionOneYear.apply(FunnelRent.instanceFunnelRentDecitions(candidateMixId,durationContract,configMapRent));
            }
        }
    }
}
