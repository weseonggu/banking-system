package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.lib.ProductType;
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
@Table(indexes = {
        @Index(name = "idx_using_product_user_id", columnList = "user_id")
})
public class UsingProduct extends AuditEntity {
    @Id
    @UuidGenerator
    @Column(name = "using_product_id")
    private UUID id;

    @Column( name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime subscriptionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProductType type;

    @Column(unique = true, name = "account_id")
    private UUID accountId;

    @Column(name = "name")
    private String name;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "is_using")
    private Boolean isUsing;


    ///////////////////////////////////////////////////////////////////////////////////////

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_in_use_id", referencedColumnName = "loan_in_use_id")
    private LoanInUse loanInUse;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "checking_in_use_id", referencedColumnName = "checking_in_use_id")
    private CheckingInUse checkingInUse;

    //////////////////////////////////////////////////////////////////////////////////////

    public static UsingProduct create(UUID userId, ProductType type, UUID accountId, String name, UUID productId){
        return UsingProduct.builder()
                .userId(userId)
                .subscriptionDate(LocalDateTime.now())
                .type(type)
                .accountId(accountId)
                .name(name)
                .productId(productId)
                .isUsing(true)
                .build();

    }
    public void addChckingInuse(CheckingInUse checkingInUse){
        this.checkingInUse = checkingInUse;
    }

    public void addLoanInuse(LoanInUse loanInUse){
        this.loanInUse = loanInUse;
    }


}
