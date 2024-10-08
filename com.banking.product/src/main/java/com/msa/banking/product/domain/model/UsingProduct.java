package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.lib.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UsingProduct extends AuditEntity {
    @Id
    @UuidGenerator
    @Column(name = "using_product_id")
    private UUID id;

    @Column(unique = true, name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime subscriptionDate;

    @Column(name = "status", nullable = false)
    private SubscriptionStatus state;

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "product_id")
    private UUID productId;

    ///////////////////////////////////////////////////////////////////////////////////////

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_in_use_id", referencedColumnName = "loan_in_use_id")
    private LoanInUse loanInUse;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "checking_in_use_id", referencedColumnName = "checking_in_use_id")
    private CheckingInUse checkingInUse;

    //////////////////////////////////////////////////////////////////////////////////////
}
