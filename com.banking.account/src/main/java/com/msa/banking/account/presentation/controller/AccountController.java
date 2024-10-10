package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.AccountService;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.account.presentation.dto.account.AccountListResponseDto;
import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.account.presentation.dto.account.AccountResponseDto;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    // 상품 가입시 계좌 등록 후 계좌 ID 반환
    // TODO: requestDto가 record타입인 경우 final을 붙이는게 의미가 있는가? AuditEntity에서 createdBy 처리
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<UUID> createAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody final AccountRequestDto request) {

        System.out.println(request.type());
        System.out.println(request.status());
        return ResponseEntity.ok(accountService.createAccount(request, userDetails.getUsername()));
    }


    // 계좌 상태 변경
    @PatchMapping("/{account_id}/status")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<AccountResponseDto> updateStatusAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam AccountStatus status) {

        return ResponseEntity.ok(accountService.updateAccountStatus(accountId, status, userDetails.getUsername(), userDetails.getRole()));
    }

    // 계좌 비밀번호 변경
    @PatchMapping("/{account_id}/accountPin")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<Void> updateAccountPin(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam String pin) {

        accountService.updateAccountPin(accountId, pin, userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.noContent().build();
    }

    // 계좌 해지
    @DeleteMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId) {

        accountService.deleteAccount(accountId, userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.noContent().build();
    }


    // 계좌 전체 조회
    // TODO: QueryDSL 구현체 작성.
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    public ResponseEntity<Page<AccountListResponseDto>> getAccounts(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody AccountSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(accountService.getAccounts(search, pageable));
    }


    // 계좌 상세 조회
    @GetMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<AccountResponseDto> getAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId) {

            return ResponseEntity.ok(accountService.getAccount(accountId, userDetails.getUsername(), userDetails.getRole()));
    }
}
