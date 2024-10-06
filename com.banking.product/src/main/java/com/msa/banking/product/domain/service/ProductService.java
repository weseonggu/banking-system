package com.msa.banking.product.domain.service;

import com.msa.banking.product.application.dto.ResponseProductPage;
import com.msa.banking.product.config.redis.RedisCacheKey;
import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.ProductRepository;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 테이블 데이터 저장
    @Transactional
    public Product saveProduct(Product product) {

        return productRepository.save(product);
    }

    // 입출금 상품 디테일 저장
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheKey.ProdctListCache, allEntries = true, beforeInvocation = true)
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
            @CacheEvict(cacheNames = RedisCacheKey.ProdctListCache, allEntries = true, beforeInvocation = true)
    })
    public void saveLoanProduct(Product product, LoanDetail loanDetail, PDFInfo pdf) {
        product.addDetail(loanDetail.addPDF(pdf));
        product.changeIsFinish();
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisCacheKey.ProdctListCache, key = "#condition.type + '_' + #pageable.getSort()")
    public List<ResponseProductPage> findAllProducts(Pageable pageable, RequestSearchProductDto condition) {
        return productRepository.findAllProduct(pageable, condition);
    }
}
