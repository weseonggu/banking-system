package com.msa.banking.product.domain.model;

import com.msa.banking.product.lib.LoanState;
import jakarta.persistence.*;
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

    @Column(name = "loan_amount", nullable = false)
    private double loanAmount;

    @Column(precision = 6, scale = 4, name = "interest_rate",  nullable = false)
    private BigDecimal interestRate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "status", nullable = false)
    private LoanState status;

    ///////////////////////////////////////////////////////////////////////

    @OneToOne(mappedBy = "loanInUse", fetch = FetchType.LAZY)
    private UsingProduct usingProduct;

    ///////////////////////////////////////////////////////////////////////






}
