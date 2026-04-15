package com.realstate.habitar.infraestructure.classes.model;

import com.realstate.habitar.domain.enums.CalendarYear;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "payment_plan")
public class PaymentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_liquidate_id")
    private DealLiquidate dealLiquidate;

    @Column(name = "trace_first_date")
    private LocalDateTime traceFirstDate;

    @Setter
    @Column(name = "trace_second_date")
    private LocalDateTime traceSecondDate;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "rest_days")
    private Long restDays;

    @Setter
    @Column(name = "is_pay")
    private boolean isPay = false;
}
