package com.msa.banking.product.domain.service;

import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.ProductRepository;
import com.msa.banking.product.presentation.exception.custom.CustomDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
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
            // 데이터베이스에 데이터를 삽입하는 로직
            product.addDetail( detail.addPDF(pdf));
            product.changeIsFinish();
            productRepository.save(product);



    }

    public void saveLoanProduct(Product product, LoanDetail loanDetail, PDFInfo pdf) {
        product.addDetail(loanDetail.addPDF(pdf));
        product.changeIsFinish();
        productRepository.save(product);
    }
}
