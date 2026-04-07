package com.realstate.habitar.domain.baselogic.interfaces;

import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;

public interface VisitorExtractSales<R>{
    R extract(DetailPaymentFunnel detailPaymentFunnel);
    R extract(Resume resume);
}
