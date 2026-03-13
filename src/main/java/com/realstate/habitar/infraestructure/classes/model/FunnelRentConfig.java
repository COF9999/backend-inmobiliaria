package com.realstate.habitar.infraestructure.classes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "funnel_rent_config")
public class FunnelRentConfig {
    @Id
    private Long dealId;

    @Column(name = "total-percentage")
    private Double totalPercentage;

    @Column(name = "percentage_first_agent")
    private Double percentajeFirstAgent;

    @Column(name = "percentage_second_agent")
    private Double percentageSecondAgent;
}
