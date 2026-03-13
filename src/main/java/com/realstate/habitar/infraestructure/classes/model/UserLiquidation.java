package com.realstate.habitar.infraestructure.classes.model;

import com.realstate.habitar.global.infraestructure.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "user_liquidation")
public class UserLiquidation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(name = "total_amount",precision = 12,scale = 0)
    private BigDecimal totalAmount;
}
