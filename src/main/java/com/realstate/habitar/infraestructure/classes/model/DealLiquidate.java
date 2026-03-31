package com.realstate.habitar.infraestructure.classes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "deal_liquidate")
public class DealLiquidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "deal_registry_id",
            nullable = false,
            unique = true
    )
    private DealRegistry dealRegistry;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "deal_liquidate_user",
            joinColumns = @JoinColumn(name = "deal_liquidate_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"deal_liquidate_id","user_id"})
            }
    )
    private Set<User> setUsers;

    private Integer state;
}
