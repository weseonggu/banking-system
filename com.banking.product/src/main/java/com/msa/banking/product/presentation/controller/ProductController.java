package com.msa.banking.product.presentation.controller;

import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.ProductResponseDto;
import com.msa.banking.product.presentation.response.ResponseProductPage;
import com.msa.banking.product.application.service.ProductApplicationService;
import com.msa.banking.product.presentation.request.RequestCreateCheckingProduct;
import com.msa.banking.product.presentation.request.RequestCreateLoanProduct;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    public ResponseEntity<?> createCheckingProduct(@Valid @RequestBody RequestCreateCheckingProduct product) {
        // 어플리케이션 계층 서비스 호츌
        applicationService.createCheckingProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "대출 상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "등록 중 실패")
    })
    @PostMapping(value = "/create/loan")
    @LogDataChange
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    public ResponseEntity<?> createLoanProduct(@Valid @RequestBody RequestCreateLoanProduct product) {
        // 어플리케이션 계층 서비스 호츌
        applicationService.createLoanProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "금융 상품 목록 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록"),
            @ApiResponse(responseCode = "401", description = "권한이 없음")
    })
    @GetMapping(value = "/board")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findProucts(Pageable pageable, RequestSearchProductDto condition) {
        // 어플리케이션 계층 서비스 호츌

        List<ResponseProductPage> list = applicationService.findAllProduct(pageable, condition);
        SuccessResponse response =  new SuccessResponse(
                HttpStatus.OK.value(),
                "상품 목록",
                list
        );
        return ResponseEntity.ok(response);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "금융 상품 상세 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록"),
            @ApiResponse(responseCode = "401", description = "권한이 없음")
    })
    @GetMapping(value = "/detail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findProuctDetail(@RequestParam("product_id") UUID productId) {
        // 어플리케이션 계층 서비스 호츌
        ProductResponseDto dto = applicationService.findProductDetail(productId);
        SuccessResponse response =  new SuccessResponse(
                HttpStatus.OK.value(),
                "상품 디테일",
                dto
        );
        return ResponseEntity.ok(response);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ResponseEntity<?> deleteProduct(@RequestParam("product_id") UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        applicationService.deleteProduct(productId, userDetails);
        SuccessResponse response =  new SuccessResponse(
                HttpStatus.OK.value(),
                "상품을 삭제 했습니다.",
                null
        );
        return ResponseEntity.ok(response);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PutMapping("/add/like")
    @PreAuthorize("isAuthenticated()")
    public void addLike(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("product_id") UUID productId){
        applicationService.addLike(userDetails, productId);
    }
    @PutMapping("/delete/like")
    @PreAuthorize("isAuthenticated()")
    public void deleteLike(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("product_id") UUID productId){
        applicationService.deleteLike(userDetails, productId);
    }


}
