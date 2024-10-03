package com.msa.banking.product.application.service;

import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductApplicationService {



    @Transactional
    public void createCheckingProduct(RequestCreateCheckingProduct product) {

    }
}
