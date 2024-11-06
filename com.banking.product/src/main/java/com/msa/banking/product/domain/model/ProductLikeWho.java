package com.msa.banking.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product_like_who"
//        ,
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"product_id", "like_id"})
//        }
)
public class ProductLikeWho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_id", nullable = false)
    private ProductLike productLike;

    ///////////////////////////////////////////////////////////////////////////////

    public static ProductLikeWho create(ProductLike like, UUID userId) {
        return ProductLikeWho.builder().productLike(like).userId(userId).build();
    }
}
