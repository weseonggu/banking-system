package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.TransactionsService;
import com.msa.banking.account.presentation.dto.transactions.TransactionRequestDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsListResponseDto;
import com.msa.banking.account.presentation.dto.transactions.TransactionsSearchRequestDto;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account-transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }


    // 입금 거래 생성
    @PostMapping("/{account_id}/deposit")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<TransactionResponseDto> createDeposit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody TransactionRequestDto request) {

        return ResponseEntity.ok(transactionsService.createDeposit(accountId, request,  userDetails.getUsername(), userDetails.getRole()));
    }




    // 출금 거래 생성
    @PostMapping("/{account_id}/withdrawal")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<TransactionResponseDto> createWithdrawal(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam String accountPin,
            @RequestBody TransactionRequestDto request) {

        return ResponseEntity.ok(transactionsService.createWithdrawal(accountId, accountPin, request, userDetails.getUsername(), userDetails.getRole(), userDetails.getUserId()));
    }


    // 이체 거래 생성
    @PostMapping("/{account_id}/transfer")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<Void> createTransfer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam String accountPin,
            @RequestBody TransactionRequestDto request) {

        transactionsService.createTransfer(accountId, accountPin, request, userDetails.getUsername(), userDetails.getRole(), userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }


    // 거래 설명 수정
    @PatchMapping("/{transaction_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("transaction_id") Long transactionId,
            @RequestParam String description) {

        return ResponseEntity.ok(transactionsService.updateTransaction(transactionId, description,userDetails.getUsername(), userDetails.getRole()));
    }


    // 거래 전체 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    public ResponseEntity<Page<TransactionsListResponseDto>> getTransactions(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody TransactionsSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionsService.getTransactions(search, pageable));
    }


    // 거래 상세 조회
    @GetMapping("/{transaction_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<TransactionResponseDto> getTransaction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("transaction_id") Long transactionId) {

        return ResponseEntity.ok(transactionsService.getTransaction(transactionId, userDetails.getUsername(), userDetails.getRole()));
    }
}
