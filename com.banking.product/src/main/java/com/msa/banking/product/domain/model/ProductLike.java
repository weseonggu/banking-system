package com.msa.banking.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "like_id")
    private Long id;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Version
    private Long version;
    ///////////////////////////////////////////////////////////////////////////////

    @OneToOne(mappedBy = "productLike", fetch = FetchType.LAZY)
    private Product product;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductLikeWho> likes = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////////

    public static ProductLike create() {
        return ProductLike.builder().likeCount(0L).build();
    }
    public void addLike(ProductLikeWho productLikeWho){
        this.likes.add(productLikeWho);
        this.likeCount++;
    }
    public void removeLike(ProductLikeWho productLikeWho){
        this.likes.remove(productLikeWho);
        this.likeCount--;
    }
}
