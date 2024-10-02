package com.msa.banking.product.presentation.controller;

import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.presentation.request.RequestCreateProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    @PostMapping("/create")
    @LogDataChange
    public ResponseEntity<String> createProduct(@RequestBody RequestCreateProduct product) {

        return new ResponseEntity<>(product.name(), HttpStatus.OK);
    }
}
