package com.msa.banking.product.presentation.controller;

import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.product.application.service.PDFInfoApplicationService;
import com.msa.banking.product.application.service.ProductApplicationService;
import com.msa.banking.product.application.service.UploadService;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import com.msa.banking.product.presentation.request.RequestCreateLoanProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.Consumes;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService applicationService;



    @Operation(summary = "입출금 상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/create/checking")
    @LogDataChange
    // TODO: 관리자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<?> createCheckingProduct(@RequestBody RequestCreateCheckingProduct product) {
        // 어플리케이션 계층 서비스 호츌
        applicationService.createCheckingProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "대출 상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/create/loan")
    @LogDataChange
    // TODO: 관리자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<?> createLoanProduct(@RequestBody RequestCreateLoanProduct product) {
        // 어플리케이션 계층 서비스 호츌
        applicationService.createLoanProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }




}
