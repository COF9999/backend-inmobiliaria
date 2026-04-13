package com.realstate.habitar.infraestructure.classes.model;

import com.realstate.habitar.domain.converters.StateObjectConverter;
import com.realstate.habitar.domain.enums.StatesObjects;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@AllArgsConstructor
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

    @OrderColumn(name = "order_index")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "deal_liquidate_user",
            joinColumns = @JoinColumn(name = "deal_liquidate_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"deal_liquidate_id","user_id"})
            }
    )
    private List<User> listUsers;

    @Column(name = "created")
    private LocalDateTime created;

    @Setter
    @Column(name = "state", columnDefinition = "TINYINT")
    @Convert(converter = StateObjectConverter.class)
    private StatesObjects state;
}
