package com.realstate.habitar.application.implementations.processDeal;

import com.realstate.habitar.application.usecases.processDeal.ProcessDealService;
import com.realstate.habitar.domain.dtos.processDeal.ProcessDealResponseDto;
import com.realstate.habitar.infraestructure.adapters.interfaces.processDeal.ProcessedDealRepository;
import com.realstate.habitar.infraestructure.classes.model.ProcessedDeal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessDealImpl implements ProcessDealService {

    private final ProcessedDealRepository processedDealRepository;

    public ProcessDealImpl(ProcessedDealRepository processedDealRepository){
        this.processedDealRepository = processedDealRepository;
    }

    @Override
    @Transactional
    public List<ProcessDealResponseDto> getProcessDeals() {
        return processedDealRepository.getProcessDeals()
                .stream()
                .map(this::convertTo)
                .toList();
    }

    @Override
    public ProcessDealResponseDto convertTo(ProcessedDeal processedDeal) {
        return new ProcessDealResponseDto(processedDeal.getDealId(),
                processedDeal.getPipelineType(),
                processedDeal.getUserLiquidationId(),
                processedDeal.getAmount(),
                processedDeal.getClosedAt(),
                processedDeal.getProcessedAt());
    }
}
