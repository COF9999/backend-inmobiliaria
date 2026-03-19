package com.realstate.habitar.application.usecases.processDeal;

import com.realstate.habitar.domain.dtos.processDeal.ProcessDealResponseDto;
import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;

import java.util.List;

public interface ProcessDealService {

    List<ProcessDealResponseDto> getProcessDeals();

    ProcessDealResponseDto convertTo(ProcessedDeal processedDeal);
}
