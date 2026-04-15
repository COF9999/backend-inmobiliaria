package com.realstate.habitar.domain.dtos.funnelsDtos.sales;

import com.realstate.habitar.domain.baselogic.interfaces.DetailVisitorSales;
import com.realstate.habitar.domain.baselogic.interfaces.VisitorExtractSales;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public abstract class DetailResumeAbstract {
    public abstract <R> R accept(DetailVisitorSales<R> detailVisitorSales);

    public abstract <R> R report(VisitorExtractSales<R> visitorReportSales);
}
