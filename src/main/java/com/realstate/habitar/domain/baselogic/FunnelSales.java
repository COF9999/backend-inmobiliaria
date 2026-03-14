package com.realstate.habitar.domain.baselogic;
import com.realstate.habitar.domain.dtos.sales.SalesToAppRecord;
import com.realstate.habitar.domain.dtos.sales.SalesToDomainRecord;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class FunnelSales {

    public SalesToAppRecord operate(SalesToDomainRecord salesToDomainRecord){
        BigDecimal scaleTax = new BigDecimal("100");
        String amountFromHubspot = salesToDomainRecord.hubspotDealDtoApp().properties().get("amount");
        Long amountToFindByRange = Long.parseLong(amountFromHubspot);
        SalesCommissionScale salesCommissionScale = findSaleCommisionByRange(salesToDomainRecord.listSalesCommissionScales(),amountToFindByRange);
        BigDecimal amountToOperate = new BigDecimal(amountFromHubspot);
        BigDecimal percentage = salesCommissionScale.getCommissionPercentage();
        BigDecimal amountCompany = BigDecimal.ZERO;
        BigDecimal amountAgent = BigDecimal.ZERO;
        if (salesToDomainRecord.isOwner()){
            // Owner
            amountCompany = applyPercentage(amountToOperate,salesToDomainRecord.percentageSale(),scaleTax);
        }else {
            // Agent
            amountAgent = applyPercentage(amountToOperate,percentage,scaleTax);
            amountCompany = applyPercentage(amountToOperate,salesToDomainRecord.percentageSale().subtract(percentage),scaleTax);
        }
        return new SalesToAppRecord(amountAgent,amountCompany);
    }

    private BigDecimal applyPercentage(BigDecimal amountToOperate, BigDecimal percentageToOperate, BigDecimal scaleTax){
        return amountToOperate.multiply(percentageToOperate)
                .divide(scaleTax,0,RoundingMode.HALF_UP);
    }

    private SalesCommissionScale findSaleCommisionByRange(List<SalesCommissionScale> salesCommissionScales, Long value){
        for (SalesCommissionScale s: salesCommissionScales){
            if (value >= s.getLowerLimit()  &&  value <= s.getUpperLimit()){
                return s;
            }
        }
        throw new IllegalArgumentException("Scale Not Found");
    }
}
