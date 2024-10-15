package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.AccountTransactionsService;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransferTransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.WithdrawalTransactionRequestDto;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account-transactions")
@Tag(name = "거래 서비스", description = "입출금, 이체, 대출에 대한 API 입니다.")
public class TransactionsController {

    private final AccountTransactionsService transactionsService;

    public TransactionsController(AccountTransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }


    // 입금 거래 생성
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "입금 거래 생성", description = "입금 거래 생성 API 입니다.")
    public ResponseEntity<?> createDeposit(
            @RequestBody DepositTransactionRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "입금 거래 완료",
                        transactionsService.createDeposit(request)
                )
        );
    }

    // 대출액 입금 생성
    @PostMapping("/{account_id}/loan-deposit")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    @Operation(summary = "대출액 입금 생성", description = "대출액 입금 생성 API 입니다.")
    public ResponseEntity<?> createLoanDeposit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody DepositTransactionRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "대출액 입금 완료",
                        transactionsService.createLoanDeposit(accountId, request, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 출금 거래 생성
    @PostMapping("/{account_id}/withdrawal")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "출금 거래 생성", description = "출금 거래 생성 API 입니다.")
    public ResponseEntity<?> createWithdrawal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody WithdrawalTransactionRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "출금 거래 완료",
                        transactionsService.createWithdrawal(accountId, request, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 이체 거래 생성
    @PostMapping("/{account_id}/transfer")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "이체 거래 생성", description = "이체 거래 생성 API 입니다.")
    public ResponseEntity<?> createTransfer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody TransferTransactionRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "이체 거래 완료",
                        transactionsService.createTransfer(accountId, request, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 거래 설명 수정
    @PatchMapping("/{transaction_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "거래 설명 수정", description = "거래 설명 수정 API 입니다.")
    public ResponseEntity<?> updateTransaction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("transaction_id") Long transactionId,
            @RequestParam String description) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.UPDATE_SUCCESS.getStatus(),
                        "거래 설명 변경 완료",
                        transactionsService.updateTransaction(transactionId, description,userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 거래 전체 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "거래 전체 조회", description = "거래 전체 조회 API 입니다.")
    public ResponseEntity<?> getTransactions(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody TransactionsSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "거래 전체 조회 완료",
                        transactionsService.getTransactions(search, pageable)
                )
        );
    }

    // 거래 상세 조회
    @GetMapping("/{transaction_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "거래 상세 조회", description = "거래 상세 조회 API 입니다.")
    public ResponseEntity<?> getTransaction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("transaction_id") Long transactionId) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "계좌 상세 조회 완료",
                        transactionsService.getTransaction(transactionId, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }
}