package com.msa.banking.product.domain.repository;

import com.msa.banking.product.application.dto.ResponseProductPage;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepositoryCustom {
    List<ResponseProductPage> findAllProduct(Pageable pageable, RequestSearchProductDto condition);
}
