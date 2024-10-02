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
@Table(name = "p_checking_detail")
public class CheckingDetail extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "checking_detail_id")
    private UUID id;

    @Column(columnDefinition = "TEXT", name = "checking_detail", nullable = false)
    private String checkingDetail;

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
