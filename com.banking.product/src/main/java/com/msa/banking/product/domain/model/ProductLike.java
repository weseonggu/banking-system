package com.msa.banking.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product_like"
//        ,
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"product_id", "user_id"})
//        }
)
public class ProductLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    ///////////////////////////////////////////////////////////////////////////////

    public static ProductLike create(Product product, UUID userId) {
        return ProductLike.builder().product(product).userId(userId).build();
    }
}
