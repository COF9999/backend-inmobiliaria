package com.realstate.habitar.domain.dtos.dealRegister;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealRegisterDto(String dealUserId, String pipelineType, Long userHubId, BigDecimal amount,
                              LocalDateTime closedAt) {
}
