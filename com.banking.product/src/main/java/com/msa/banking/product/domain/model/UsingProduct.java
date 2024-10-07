package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.lib.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(unique = true, name = "user_id")
    private UUID userId;

    @Column(name = "subscription_date")
    private LocalDateTime subscriptionDate;

    @Column(name = "status")
    private SubscriptionStatus state;

    @Column(name = "account_id")
    private UUID accountId;
}
