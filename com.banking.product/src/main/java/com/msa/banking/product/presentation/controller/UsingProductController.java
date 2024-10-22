package com.msa.banking.product.presentation.controller;

import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.application.dto.NewSubscriber;
import com.msa.banking.product.application.dto.UsingProductDetailDto;
import com.msa.banking.product.application.dto.UsingProductPage;
import com.msa.banking.product.application.dto.UsingProductResponseDto;
import com.msa.banking.product.application.service.UsingProductService;
import com.msa.banking.product.presentation.request.LoanRunRequest;
import com.msa.banking.product.presentation.request.RequestJoinLoan;
import com.msa.banking.product.presentation.request.RequestUsingProductConditionDto;
import com.msa.banking.product.presentation.request.RequsetJoinChecking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            @ApiResponse(responseCode = "404", description = "인증 과정에서 데이터가 틀린 경우"),
            @ApiResponse(responseCode = "500", description = "가입 중 실패")
    })
    @PostMapping(value = "/join/checking")
    @LogDataChange
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'MANAGER')")
    public ResponseEntity<?> signUpForCheckingProduct(
            @Valid @RequestBody RequsetJoinChecking requsetJoinChecking,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        NewSubscriber dto = usingProductService.joinChecking(requsetJoinChecking, userDetails);
        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "입 출금 상품을 가입했습니다.",
                dto
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "대출 상품 가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 가입 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "404", description = "인증 과정에서 데이터가 틀린 경우"),
            @ApiResponse(responseCode = "500", description = "가입 중 실패")
    })
    @PostMapping(value = "/join/loan")
    @LogDataChange
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<?> signUpForLoanProduct(
            @Valid @RequestBody RequestJoinLoan requestJoinLoan,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        NewSubscriber dto  = usingProductService.joinLoan(requestJoinLoan, userDetails);

        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "대출 상품을 가입했습니다.",
                dto
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 가입 상품 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 상품 목록"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "404", description = "조회 조건 틀림"),
            @ApiResponse(responseCode = "500", description = "조회 실패")
    })
    @GetMapping(value = "/using/financial")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponse> findUsingFinancial(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              RequestUsingProductConditionDto condition,
                                                              Pageable page) {
        List<UsingProductPage> data  = usingProductService.fingUsingProductPage(page, condition, userDetails);
        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "가입한 상품 목록입니다.",
                data
        );
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "대출 신청 결과 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대출 승인"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "404", description = "대출 신청 불가"),
            @ApiResponse(responseCode = "500", description = "승인 실패")
    })
    @PatchMapping(value = "/using/loan/approval")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'MASTER')")
    @LogDataChange
    public ResponseEntity<SuccessResponse> approvalLoan(@RequestParam("using_product_id") UUID id,
                                                        @RequestParam("choice") boolean choice,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails){

        boolean result = usingProductService.changeLoanSate(id, userDetails, choice);

        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                String.format("대출을 %s 했습니다.", result? "승인" : "거부"),
                ""
        );
        return ResponseEntity.ok(response);
    }


    // 대출 실행
    @Operation(summary = "대출 실행 요청 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대출 실행"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "실행 실패")
    })
    @PatchMapping(value = "/using/loan/running")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @LogDataChange
    public ResponseEntity<SuccessResponse> runningLoan(@RequestBody LoanRunRequest dto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails){

        usingProductService.changeLoanSateToRun(dto.getAccountId(), userDetails, dto.getAccountNumber());

        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "대출을 실행 했습니다.",
                ""
        );
        return ResponseEntity.ok(response);
    }



    // 사용중인 상품 상세 조회
    @Operation(summary = "사용중인 상품 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대출 실행"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "실행 실패")
    })
    @GetMapping(value = "/using/product/detail")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'MANAGER', 'MASTER')")
    public ResponseEntity<SuccessResponse> findUsingProductDetail(@RequestParam("using_product_id") UUID id,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails){

        UsingProductDetailDto dto = usingProductService.findUsingProductDetail(id, userDetails);

        SuccessResponse response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "사용중인 상품 상세 내용입니다.",
                dto
        );
        return ResponseEntity.ok(response);
    }

    // TODO: 상품 가입 해지
    @Operation(summary = "상품 가입 해지")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 해지 됨"),
            @ApiResponse(responseCode = "500", description = "실행 실패")
    })
    @PatchMapping("/termination/product")
    @LogDataChange
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'MANAGER')")
    public ResponseEntity<SuccessResponse> terminationUsingProduct(@RequestParam("using_product") UUID usingProduct,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails){

        boolean result =  usingProductService.terminationProduct(usingProduct, userDetails);
        SuccessResponse response;
        if(result){
            response = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    "가입하신 상품을 해지했습니다.",
                    ""
            );
        }else {
            response = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    "가입하신 상품의 해지에 실패 했습니다. 잠시후 다시 시도해주세요.",
                    ""
            );
        }

        return ResponseEntity.ok(response);
    }


 //////////////////////////////////////////////    다른 마이크로 서비스 요청     /////////////////////////////////////////////////////////
    // AccountId로 UsingProduct 조회
    @Operation(summary = "accountId로 UsingProduct 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없음"),
            @ApiResponse(responseCode = "500", description = "조회 실패")
    })
    @GetMapping("/find/{accountId}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> findByAccountId(@PathVariable UUID accountId, @AuthenticationPrincipal UserDetailsImpl userDetails){

        String userRole = userDetails.getRole();
        UUID userId = userDetails.getUserId();

        UsingProductResponseDto responseDto = usingProductService.findByAccountId(accountId, userId, userRole);

        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "findByAccountId", responseDto));
    }
}
