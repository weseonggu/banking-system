package com.msa.banking.product.domain.repository;

import com.msa.banking.product.application.dto.UsingProductPage;
import com.msa.banking.product.presentation.request.RequestUsingProductConditionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsingProductRepositoryCustom {
    List<UsingProductPage> findAllUsingProductPages(Pageable page, RequestUsingProductConditionDto condition);
}
