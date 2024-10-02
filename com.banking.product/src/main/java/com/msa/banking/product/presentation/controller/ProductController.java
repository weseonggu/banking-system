package com.msa.banking.product.presentation.controller;

import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.product.application.service.ProductApplicationService;
import com.msa.banking.product.application.service.UploadService;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
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
public class ProductController {

    private final ProductApplicationService applicationService;
    private final UploadService uploadService;

    public ProductController(ProductApplicationService applicationService, UploadService uploadService) {
        this.applicationService = applicationService;
        this.uploadService = uploadService;
    }

    @Operation(summary = "입출금 상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/create/checking"
    , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @LogDataChange
    // TODO: 관리자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<?> createProduct(@RequestPart("product") RequestCreateCheckingProduct product,
                                                @RequestPart("pdf") MultipartFile pdfFile) {
        // 어플리케이션 계층 서비스 호츌
        applicationService.createProductSynchronous(product, pdfFile);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "pdf 저장 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pdf 파일 저장 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/upload"
            , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @LogDataChange
    // TODO: 관리자만 접근 가능하도록 @hasAnyAuthority() 설정 해야함
    public ResponseEntity<?> UploadPdf(@RequestPart("pdf") MultipartFile pdfFile) {
        // 어플리케이션 계층 서비스 호츌
        uploadService.uploadImage(pdfFile);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @Operation(summary = "pdf 다운로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pdf 파일"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "404", description = "파일 없음"),
            @ApiResponse(responseCode = "500", description = "다운 실패")
    })
    @GetMapping("/download/pdf")
    public ResponseEntity<InputStreamResource> getImage(@RequestParam(value = "file") String fileName) throws IOException {

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        InputStream imageStream = uploadService.getFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(imageStream));
    }
}
