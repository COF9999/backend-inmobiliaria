package com.realstate.habitar.domain.dtos.funnelsDtos.sales;

import com.realstate.habitar.domain.baselogic.interfaces.DetailVisitorSales;
import com.realstate.habitar.domain.baselogic.interfaces.VisitorExtractSales;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@AllArgsConstructor
public class Resume extends DetailResumeAbstract {
    String nameUser;
    String hubId;
    BigDecimal totalAmount;
    Integer elementsSelected;

    @Override
    public <R> R accept(DetailVisitorSales<R> detailVisitorSales) {
        return detailVisitorSales.visit(this);
    }

    @Override
    public <R> R report(VisitorExtractSales<R> visitorReportSales){
        return visitorReportSales.extract(this);
    }
}
