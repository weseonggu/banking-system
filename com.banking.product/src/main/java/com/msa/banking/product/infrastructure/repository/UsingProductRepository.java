package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.domain.repository.UsingProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsingProductRepository extends JpaRepository<UsingProduct, UUID>, UsingProductRepositoryCustom {
}
