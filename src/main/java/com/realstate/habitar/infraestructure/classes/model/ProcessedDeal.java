package com.realstate.habitar.infraestructure.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "processed_deal")
public class ProcessedDeal {
    @Id
    private String dealId;

    @Column(name = "pipeline_type")
    private String pipelineType;

    @Column(name = "user_liquidation_id",nullable = false)
    private Long userLiquidationId;

    @Column(name = "amount",precision = 12, scale = 0)
    private BigDecimal amount;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
