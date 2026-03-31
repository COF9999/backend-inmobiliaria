package com.realstate.habitar.infraestructure.adapters.interfaces.processDeal;

import java.util.List;

public interface ProcessedDealRepository {
    boolean isExistKey(String key);

    boolean notIsProcessed(String key);

   // List<ProcessedDeal> getProcessDeals();
}
