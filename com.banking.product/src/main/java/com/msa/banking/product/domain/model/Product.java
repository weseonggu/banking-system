package com.msa.banking.product.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.product.lib.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product"
        , indexes = {
        @Index(name = "idx_product_created_at", columnList = "created_at")
        }
)
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

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "is_finish", nullable = false)
    private Boolean isFinish;

    @Column(name = "like_count")
    private Integer likeCount;

    ///////////////////////////////////////////////////////////////////////////////////////

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_detail_id", referencedColumnName = "loan_detail_id")
    private LoanDetail loanDetail;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "checking_detail_id", referencedColumnName = "checking_detail_id")
    private CheckingDetail checkingDetail;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductLike> likes = new ArrayList<>();

    //////////////////////////////////////////////////////////////////////////////////////

    public static Product create(String name, ProductType type, LocalDateTime validFrom, LocalDateTime validTo) {
        return Product.builder()
                .name(name)
                .type(type)
                .validFrom(validFrom == null ? LocalDateTime.now() : validFrom)
                .validTo(validTo)
                .isFinish(false)
                .likeCount(0)
                .build();
    }

    public void  createMappingCheckingDetail(CheckingDetail checkingDetail) {
        this.checkingDetail = checkingDetail;
    }
    public void addDetail(CheckingDetail checkingDetail){
        this.checkingDetail = checkingDetail;
    }
    public void addDetail(LoanDetail loanDetail){
        this.loanDetail = loanDetail;
    }
    public void changeIsFinish(){
        this.isFinish = true;
    }

    public void addLike(ProductLike productLike){
        this.likes.add(productLike);
        this.likeCount++;
    }
    public void removeLike(ProductLike productLike){
        this.likes.remove(productLike);
        this.likeCount--;
    }
}
