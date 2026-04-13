package com.realstate.habitar.application.components;

import com.realstate.habitar.application.components.helpers.sales.SalesAsync;
import com.realstate.habitar.application.components.helpers.sales.SalesHelper;
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
import com.realstate.habitar.infraestructure.adapters.interfaces.salesCommission.SalesCommissionScaleRepository;
import com.realstate.habitar.infraestructure.config.data.GlobalVariablesCache;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;
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


    public FunnelSalesComponent(
                                SalesCommissionScaleRepository salesCommissionScaleRepository,
                                FunnelSales funnelSales,
                             //   DaoCrudPort<ProcessedDeal> processedDealDaoCrudPort,
                                GlobalVariablesCache globalVariablesCache,
                                UserServiceBasicOperations userServiceBasicOperations,
                                SalesHelper salesHelper,
                                SalesAsync salesAsync
                               ){
        this.salesCommissionScaleRepository = salesCommissionScaleRepository;
        this.funnelSales = funnelSales;

      //  this.processedDealDaoCrudPort = processedDealDaoCrudPort;
        this.globalVariablesCache = globalVariablesCache;
        this.userServiceBasicOperations = userServiceBasicOperations;
        this.salesHelper = salesHelper;
        this.salesAsync = salesAsync;
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
            return new CalculateOwnerLiquidationRecord(resultOperation,dealsProccesed);
        }catch (Exception e){
            throw e;
        }
    }

    private void closeDealsAlreadyProccessed(Set<HubspotDealDtoApp> dealsProccesed) {
        for (HubspotDealDtoApp deal:dealsProccesed){
           // Long idUserLiquidator = userLiquidationRepository.getIdUserLiquidation(deal.ownerId());

           /* ProcessedDeal processedDeal = new ProcessedDeal(
                    deal.id()+"-"+deal.ownerId(),
                    deal.pipelineType(),
                    idUserLiquidator,
                    new BigDecimal(deal.properties().get("amount")),
                    OffsetDateTime.parse(deal.properties().get("closedate")).toLocalDateTime(),
                    LocalDateTime.now());
            processedDealDaoCrudPort.create(processedDeal);

            */
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
