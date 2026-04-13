package com.realstate.habitar.infraestructure.classes.model;


import com.realstate.habitar.domain.converters.StateObjectConverter;
import com.realstate.habitar.domain.enums.StatesObjects;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
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

    @Column(name = "name_user")
    private String nameUser;

    @Column(name = "pipeline_type",length = 100)
    private String pipelineType;

    @Column(name = "user_hub_id",nullable = false)
    private Long hubId;

    @Column(name = "amount",precision = 12, scale = 0)
    private BigDecimal amount;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Setter
    @Column(name = "state", columnDefinition = "TINYINT")
    @Convert(converter = StateObjectConverter.class)
    private StatesObjects state;

    @Column(name = "created")
    private LocalDateTime created;
}
