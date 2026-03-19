package com.realstate.habitar.domain.dtos.processDeal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProcessDealResponseDto(String id, String pipelineType, Long userLiquidationId, BigDecimal amount,
                                     LocalDateTime closedAt,LocalDateTime processedAt) {
}
