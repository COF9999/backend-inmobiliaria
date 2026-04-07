package com.realstate.habitar.infraestructure.classes.abstraxct;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
public abstract class BaseFunnelConfigAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "key_variable")
    private String keyVariable;

    @Setter
    @Column(name = "value_variable")
    private String valueVariable;
}
