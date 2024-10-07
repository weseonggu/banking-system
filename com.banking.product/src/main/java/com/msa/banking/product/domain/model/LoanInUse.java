package com.msa.banking.product.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class LoanInUse {

    @Id
    @UuidGenerator
    @Column(name = "loan_in_use_id")
    private UUID id;

    @Column(name = "loan_amount")
    private double loanAmount;

    @Column(precision = 6, scale = 4, name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;






}
