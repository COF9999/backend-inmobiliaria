package com.realstate.habitar.infraestructure.adapters.interfaces.processDeal;

import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;

import java.util.List;

public interface ProcessedDealRepository {
    boolean isExistKey(String key);

    boolean notIsProcessed(String key);

    List<ProcessedDeal> getProcessDeals();
}
