package com.realstate.habitar.infraestructure.classes.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deal_registry")
public class DealRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deal_user_id")
    private String dealUserId;

    @Column(name = "pipeline_type",length = 100)
    private String pipelineType;

    @Column(name = "user_hub_id",nullable = false)
    private Long hubId;

    @Column(name = "amount",precision = 12, scale = 0)
    private BigDecimal amount;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(columnDefinition = "TINYINT")
    private Byte state;
}
