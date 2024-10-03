package com.msa.banking.product.domain.service;

import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.ProductRepository;
import com.msa.banking.product.presentation.exception.custom.CustomDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product saveProduct(Product product) {

        return productRepository.save(product);
    }

    @Transactional
    public void saveCheckkingProduct(Product product, CheckingDetail detail, PDFInfo pdf) {
        try {
            // 데이터베이스에 데이터를 삽입하는 로직
            product.addDetail( detail.addPDF(pdf));
            product.changeIsFinish();
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            // 중복 키 예외 처리
            throw new CustomDuplicateKeyException("이미 존재하는 키입니다.");
        }catch (Exception e){
            throw new CustomDuplicateKeyException("이미 존재하는 키입니다.");
        }


    }

}
