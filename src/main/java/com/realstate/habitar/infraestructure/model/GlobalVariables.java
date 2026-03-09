package com.realstate.habitar.infraestructure.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "global_variable")
public class GlobalVariables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "key_variable", unique = true)
    private String keyVariable;

    @Column(name = "value_variable")
    private String value;
}
