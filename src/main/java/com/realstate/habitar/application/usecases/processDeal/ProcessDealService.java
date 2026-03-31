package com.realstate.habitar.application.usecases.processDeal;

import com.realstate.habitar.domain.dtos.processDeal.ProcessDealResponseDto;

import java.util.List;

public interface ProcessDealService {

   // List<ProcessDealResponseDto> getProcessDeals();

   // ProcessDealResponseDto convertTo(ProcessedDeal processedDeal);

    boolean notIsProcessed(String key);
}
