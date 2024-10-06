package com.msa.banking.product.application.service;

import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncService {

    private final ProductService productService;

    // 상품 등록 후 바로 캐싱 비동기로
    @Async
    protected void afterCreateNewProductLoginc(){

        Sort sort = Sort.by("create_at").ascending();
        Pageable newPageable = PageRequest.of(0, 25, sort);

        RequestSearchProductDto condition = new RequestSearchProductDto();

        productService.findAllProducts(newPageable, condition);

    }
}
