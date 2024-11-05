package com.msa.banking.product.domain.repository;

import com.msa.banking.product.domain.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
}
