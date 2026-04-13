package com.realstate.habitar.infraestructure.classes.model;

import com.realstate.habitar.domain.enums.CalendarYear;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_liquidate_id")
    private DealLiquidate dealLiquidate;

    @Column(name = "trace_first_date")
    private LocalDateTime traceFirstDate;

    @Column(name = "trace_second_date")
    private LocalDateTime traceSecondDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "rest_days")
    private Long restDays;

    /*
    @Column(name = "month_number", columnDefinition = "TINYINT UNSIGNED")
    private CalendarYear monthNumber;

     */
}
