package com.msa.banking.product.presentation.controller;

import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.service.UsingProductService;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.presentation.request.RequestJoinLoan;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class UsingProductController {

    private final UsingProductService usingProductService;

    @Operation(summary = "입출금 상품 가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 가입 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "가입 중 실패")
    })
    @PostMapping(value = "/join/checking")
    @LogDataChange
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> signUpForCheckingProduct(@Valid @RequestBody RequsetJoinChecking requsetJoinChecking,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        usingProductService.joinChecking(requsetJoinChecking, userDetails);
        return null;
    }

    @Operation(summary = "대출 상품 가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 가입 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "가입 중 실패")
    })
    @PostMapping(value = "/join/loan")
    @LogDataChange
    public ResponseEntity<?> signUpForLoanProduct(@Valid @RequestBody RequestJoinLoan requsetJoinLoan) {

        return null;
    }
}
