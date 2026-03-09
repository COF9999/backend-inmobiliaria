package com.realstate.habitar.infraestructure.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "sales_commission_scale")
public class SalesCommissionScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lower_limit", columnDefinition = "BIGINT UNSIGNED")
    private Long lowerLimit;

    @Column(name = "upper_limit", columnDefinition = "BIGINT UNSIGNED")
    private Long upperLimit;

    @Column(name = "commission_percentage",precision = 5,scale = 3)
    private BigDecimal commissionPercentage;

}
