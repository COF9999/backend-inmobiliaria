package com.realstate.habitar.domain.dtos.funnelsDtos.sales;


import com.realstate.habitar.domain.baselogic.interfaces.DetailVisitorSales;
import com.realstate.habitar.domain.baselogic.interfaces.VisitorExtractSales;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DetailPaymentFunnel extends DetailResumeAbstract {
    private final DetailUserPayment detailOwnerPayment;
    private final DetailUserPayment detailAgentPayment;

    public DetailPaymentFunnel(DetailUserPayment detailOwnerPayment, DetailUserPayment detailAgentPayment) {
        this.detailOwnerPayment = detailOwnerPayment;
        this.detailAgentPayment = detailAgentPayment;
    }

    @Override
    public <R> R accept(DetailVisitorSales<R> detailVisitorSales) {
        return detailVisitorSales.visit(this);
    }

    @Override
    public <R> R report(VisitorExtractSales<R> visitorReportSales){
        return visitorReportSales.extract(this);
    }
}
