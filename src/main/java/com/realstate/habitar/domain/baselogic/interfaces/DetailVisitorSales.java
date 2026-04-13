package com.realstate.habitar.domain.baselogic.interfaces;

import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;

public interface DetailVisitorSales<R> {
    R visit(Resume resume);
    R visit(DetailPaymentFunnel detailPaymentFunnel);
}
