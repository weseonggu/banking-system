package com.msa.banking.product.domain.service;

import com.msa.banking.product.presentation.response.ResponseProductPage;
import com.msa.banking.product.config.redis.RedisCacheKey;
import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.infrastructure.repository.CheckRedisState;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CheckRedisState  checkRedisState;

    // 상품 테이블 데이터 저장
    @Transactional
    public Product saveProduct(Product product) {

        return productRepository.save(product);
    }

    // 입출금 상품 디테일 저장
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheKey.ProdctListCache, allEntries = true, beforeInvocation = true, condition = "@checkRedisState.isRedisAvailable()")
    })
    public void saveCheckingProduct(Product product, CheckingDetail detail, PDFInfo pdf) {
            // 데이터베이스에 데이터를 삽입하는 로직
            product.addDetail( detail.addPDF(pdf));
            product.changeIsFinish();
            productRepository.save(product);



    }

    // 대출 상품 디테일 저장
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheKey.ProdctListCache, allEntries = true, beforeInvocation = true, condition = "@checkRedisState.isRedisAvailable()")
    })
    public void saveLoanProduct(Product product, LoanDetail loanDetail, PDFInfo pdf) {
        product.addDetail(loanDetail.addPDF(pdf));
        product.changeIsFinish();
        productRepository.save(product);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisCacheKey.ProdctListCache, key = "#condition.type + '_' + #pageable.getSort() + '_' + #pageable.getPageNumber()",
            unless = "#result == null", condition = "@checkRedisState.isRedisAvailable()")
    public List<ResponseProductPage> findAllProducts(Pageable pageable, RequestSearchProductDto condition) {
        log.info("캐시: "+ checkRedisState.isRedisAvailable());
            return productRepository.findAllProduct(pageable, condition);
    }


    // 상품 디테일 검색
    @Transactional(readOnly = true)
    public Product findPrductInfo(UUID productId) {
        return productRepository.findEntityGrapById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }



}
