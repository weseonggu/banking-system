package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_withdrawal_detail")
public class WithdrawalDetail extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "withdrawal_detail_id")
    private UUID id;

    @Column(columnDefinition = "TEXT", name = "withdrawal_detail", nullable = false)
    private String withdrawalDetail;

    @Column(name = "terms_and_conditions", nullable = false)
    private String termsAndConditions;

    @Column(precision = 5, scale = 4, name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "fees", nullable = false)
    private int fees;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

}
