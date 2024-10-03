package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.domain.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product")
public class Product extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "product_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = true)
    private LocalDateTime validTo;

    @Column(name = "is_finish", nullable = false)
    private Boolean isFinish;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private LoanDetail loanDetail;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CheckingDetail checkingDetail;

    public static Product create(String name, ProductType type, LocalDateTime validFrom, LocalDateTime validTo) {
        return Product.builder()
                .name(name)
                .type(type)
                .validFrom(validFrom == null ? LocalDateTime.now() : validFrom)
                .validTo(validTo)
                .isFinish(false)
                .build();
    }
}
