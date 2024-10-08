package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.lib.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CheckingInUse extends AuditEntity {
    @Id
    @UuidGenerator
    @Column(name = "checking_in_use_id")
    private UUID id;

    @Column(name = "type", nullable = false)
    private ProductType type;

    @Column(precision = 5, scale = 4, name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "fee_waiver", nullable = false)
    private Boolean feeWaiver;

    ///////////////////////////////////////////////////////////////////////

    @OneToOne(mappedBy = "checkingInUse", fetch = FetchType.LAZY)
    private UsingProduct usingProduct;

    ///////////////////////////////////////////////////////////////////////



}
