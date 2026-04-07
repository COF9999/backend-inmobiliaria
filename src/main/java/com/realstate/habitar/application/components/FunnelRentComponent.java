package com.realstate.habitar.application.components;

import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.funnelsDtos.ResultDto;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailUserPayment;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;
import com.realstate.habitar.domain.dtos.hubspot.CalculateOwnerLiquidationRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.SalesToAppRecord;
import com.realstate.habitar.domain.dtos.sales.SalesToDomainRecord;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class FunnelRentComponent implements PipelineHandler {

    @Value("${config.keyowneraccount}")
    private String keyOwnerAccount;

    @Override
    public PipelineType pipelineKey() {
        return PipelineType.FUNNEL_RENT;
    }

    @Override
    public ResultDto handle(Map<String, List<HubspotDealDtoApp>> listDeals, String typeMethodFunnel) {
        return switch (typeMethodFunnel){

            case "LIQUIDATE" -> {
                generateLiquidationOneDeal(listDeals);
                yield new ResultDto("PROCESSED",null);
            }
            default -> new ResultDto("NOTMATCH",null);
        };
    }


    private void generateLiquidationOneDeal(Map<String, List<HubspotDealDtoApp>> listDeals){
        try {
            CalculateOwnerLiquidationRecord calculateOwnerLiquidationRecord = calculateOwnerLiquidation(listDeals);
           // salesHelper.runClassVisitor(calculateOwnerLiquidationRecord.resultOperation());
          //  closeDealsAlreadyProccessed(calculateOwnerLiquidationRecord.dealsProccesed());
        }catch (Exception e){
            throw e;
        }
    }


    private CalculateOwnerLiquidationRecord calculateOwnerLiquidation(Map<String,List<HubspotDealDtoApp>> listDeals) {
        /*List<SalesCommissionScale> listSalesCommissionScales = getAllSalesScales();
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

         */
        return null;
    }
}
