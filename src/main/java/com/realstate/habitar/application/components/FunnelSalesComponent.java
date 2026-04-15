package com.realstate.habitar.application.components;

import com.realstate.habitar.application.components.helpers.sales.SalesAsync;
import com.realstate.habitar.application.components.helpers.sales.SalesHelper;
import com.realstate.habitar.application.usecases.dealLiquidate.DealLiquidateService;
import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.application.usecases.user.UserServiceBasicOperations;
import com.realstate.habitar.domain.baselogic.FunnelSales;
import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.funnelsDtos.*;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailUserPayment;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;
import com.realstate.habitar.domain.dtos.hubspot.CalculateOwnerLiquidationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.SalesToAppRecord;
import com.realstate.habitar.domain.dtos.sales.SalesToDomainRecord;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.adapters.interfaces.salesCommission.SalesCommissionScaleRepository;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import com.realstate.habitar.infraestructure.classes.model.User;
import com.realstate.habitar.infraestructure.config.data.GlobalVariablesCache;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;
import com.realstate.habitar.infraestructure.config.hubspot.HubspotProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;

@Component
public class FunnelSalesComponent implements PipelineHandler {

    @Value("${config.keyowneraccount}")
    private String keyOwnerAccount;

    @Value("${config.percentagesale}")
    private String percentageSale;

    @Value("${config.export.path}")
    private String pathToExport;


    private final SalesCommissionScaleRepository salesCommissionScaleRepository;

    private final FunnelSales funnelSales;

   // private final DaoCrudPort<ProcessedDeal> processedDealDaoCrudPort;

    private final GlobalVariablesCache globalVariablesCache;

    private final UserServiceBasicOperations userServiceBasicOperations;

    private final SalesHelper salesHelper;

    private final SalesAsync salesAsync;

    private final DealRegistryService dealRegistryService;

    private final DealLiquidateService dealLiquidateService;


    public FunnelSalesComponent(SalesCommissionScaleRepository salesCommissionScaleRepository, FunnelSales funnelSales, GlobalVariablesCache globalVariablesCache, UserServiceBasicOperations userServiceBasicOperations, SalesHelper salesHelper, SalesAsync salesAsync, DealRegistryService dealRegistryService, DealLiquidateService dealLiquidateService) {
        this.salesCommissionScaleRepository = salesCommissionScaleRepository;
        this.funnelSales = funnelSales;
        this.globalVariablesCache = globalVariablesCache;
        this.userServiceBasicOperations = userServiceBasicOperations;
        this.salesHelper = salesHelper;
        this.salesAsync = salesAsync;
        this.dealRegistryService = dealRegistryService;
        this.dealLiquidateService = dealLiquidateService;
    }

    public PipelineType pipelineKey() {
        return PipelineType.FUNNELSALES;
    }

    @Override
    public ResultDto handle(String candidateMixId,Map<String, List<HubspotDealDtoApp>> listDeals, String typeMethodFunnel) {
        return switch (typeMethodFunnel){
            case "INFO"-> new ResultDto("PROCESSED",generateReport(listDeals,"PARTIAL"));
            case "LIQUIDATE" -> {
                generateLiquidationOneDeal(listDeals);
                yield new ResultDto("PROCESSED",null);
            }
            case "REPORT"->{
                generateReport(listDeals,"COMPLETE");
                yield new ResultDto("PROCESSED",null);
            }
            default -> new ResultDto("NOTMATCH",null);
        };
    }

    private <T,W,R> void FrameReports(CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord,W w,BiFunction<T,W,R> biFunction){
        for (Map.Entry<String, DetailResumeAbstract> mapOwnerLiquidation:calculateOwnerLiquidationRecord.resultOperation().entrySet()){
            biFunction.apply((T) mapOwnerLiquidation,w);
        }
    }


    private void generateLiquidationOneDeal(Map<String, List<HubspotDealDtoApp>> listDeals){
        try {
            CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord = calculateOwnerLiquidation(listDeals);
            salesHelper.runClassVisitor(calculateOwnerLiquidationRecord.resultOperation());
            closeDealsAlreadyProccessed(calculateOwnerLiquidationRecord.dealsProccesed());
        }catch (Exception e){
            throw e;
        }
    }


    private List<DetailResumeAbstract> generateReport(Map<String, List<HubspotDealDtoApp>> listDeals,String stage) {
        try {
            CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord = calculateOwnerLiquidation(listDeals);
            List<DetailResumeAbstract> detailResumeAbstractList = new ArrayList<>();
            BiFunction<Map.Entry<String,DetailResumeAbstract>, List<DetailResumeAbstract>,Void> biFunction = (mapEntry, list)->{
                list.add(mapEntry.getValue());
                return null;
            };
            FrameReports(calculateOwnerLiquidationRecord,detailResumeAbstractList,biFunction);
            if (stage.equalsIgnoreCase("PARTIAL")){
                return detailResumeAbstractList;
            }else if (stage.equalsIgnoreCase("COMPLETE")){
                salesAsync.WriteInFile(pathToExport,detailResumeAbstractList);
            }
            return null;
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
        BigDecimal fullAcumulatorCompany = BigDecimal.ZERO;
        String owner = "";
        Map<String, DetailResumeAbstract> resultOperation = new HashMap<>();
        try {
            for (Map.Entry<String, List<HubspotDealDtoApp>> dealByOwner : listDeals.entrySet()) {
                owner = dealByOwner.getKey();
                for (HubspotDealDtoApp hubspotDealDtoApp : dealByOwner.getValue()) {
                    if (hubspotDealDtoApp.ownerId().equalsIgnoreCase(valueOwnerAccount)) {
                        SalesToAppRecord salesToAppRecord = funnelSales.operate(new SalesToDomainRecord(hubspotDealDtoApp, listSalesCommissionScales, valuePercentageSale, true));
                        acumAmountCompany = acumAmountCompany.add(salesToAppRecord.acumAmountCompany());
                        fullAcumulatorCompany = fullAcumulatorCompany.add(salesToAppRecord.acumAmountCompany());
                        resultOperation.put(valueOwnerAccount,new DetailPaymentFunnel(
                                new DetailUserPayment("",valueOwnerAccount,acumAmountCompany,valuePercentageSale,salesToAppRecord.substractPercentage()),
                                null)
                        );
                    } else {
                        SalesToAppRecord salesToAppRecord = funnelSales.operate(new SalesToDomainRecord(hubspotDealDtoApp, listSalesCommissionScales, valuePercentageSale, false));
                        acumAmountAgent = acumAmountAgent.add(salesToAppRecord.acumAmountAgent());
                        acumAmountCompany = acumAmountCompany.add(salesToAppRecord.acumAmountCompany());
                        fullAcumulatorCompany = fullAcumulatorCompany.add(acumAmountCompany);
                        resultOperation.put(owner,new DetailPaymentFunnel(
                                new DetailUserPayment("",valueOwnerAccount,acumAmountCompany,valuePercentageSale,salesToAppRecord.substractPercentage()),
                                new DetailUserPayment("",owner,acumAmountAgent,salesToAppRecord.operationPercentage(),salesToAppRecord.commisionPercentage())
                        ));
                    }
                    dealsProccesed.add(hubspotDealDtoApp);
                }
                acumAmountAgent = BigDecimal.ZERO;
                acumAmountCompany = BigDecimal.ZERO;
            }


            Integer quantity = resultOperation.size();
            resultOperation.put(valueOwnerAccount, new Resume("",valueOwnerAccount,fullAcumulatorCompany,quantity));
            System.out.println("Result operation" );
                resultOperation.forEach((propitary,data)-> {
                    System.out.println(propitary);
                    System.out.println(data);
                });
            System.out.println("-----------------");
            return new CalculateOwnerLiquidationRecord(resultOperation,dealsProccesed);
        }catch (Exception e){
            throw e;
        }
    }

    private void closeDealsAlreadyProccessed(Set<HubspotDealDtoApp> dealsProccesed) {
        for (HubspotDealDtoApp deal:dealsProccesed){
           // Long idUserLiquidator = userLiquidationRepository.getIdUserLiquidation(deal.ownerId());

            String mixId = deal.id()+"-"+deal.ownerId();
            DealRegistry dealRegistry = dealRegistryService.findDealUserId(mixId).orElseThrow();
            dealRegistry.setState(StatesObjects.SUCCESFULLY);
            User user = userServiceBasicOperations.findUserByHubId(deal.ownerId()).orElseThrow();
            dealLiquidateService.createDealLiquidation(deal,List.of(user));
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
