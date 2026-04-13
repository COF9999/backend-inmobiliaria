package com.realstate.habitar.infraestructure.classes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "payment_realized")
public class PaymentRealized {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "payment_plan_id",
            unique = true,
            nullable = false)
    private PaymentPlan paymentPlan;

    private LocalDateTime paymentCloseDate;
}
