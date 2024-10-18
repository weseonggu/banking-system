package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.AccountService;
import com.msa.banking.account.presentation.dto.account.AccountSearchRequestDto;
import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "계좌 서비스", description = "계좌에 관련된 API 입니다.")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    // 상품 가입시 계좌 등록 후 계좌 ID 반환
    // TODO: requestDto가 record타입인 경우 final을 붙이는게 의미가 있는가? AuditEntity에서 createdBy 처리
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "계좌 등록", description = "상품 가입 시 계좌 등록 API 입니다.")
    public ResponseEntity<UUID> createAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody final AccountRequestDto request) {

        return ResponseEntity.ok(accountService.createAccount(request, userDetails.getUserId(), userDetails.getRole()));
    }

    // 계좌 상태 변경
    @PatchMapping("/{account_id}/status")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "계좌 상태 변경", description = "계좌의 상태를 변경 API 입니다.")
    public ResponseEntity<?> updateStatusAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam AccountStatus status) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.UPDATE_SUCCESS.getStatus(),
                        "계좌 상태 변경 완료",
                        accountService.updateAccountStatus(accountId, status)
                )
        );
    }

    // 계좌 비밀번호 변경
    @PatchMapping("/{account_id}/accountPin")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "계좌 비밀번호 변경", description = "계좌의 비밀번호를 변경 API 입니다.")
    public ResponseEntity<?> updateAccountPin(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestParam String pin) {

        accountService.updateAccountPin(accountId, pin, userDetails.getUserId(), userDetails.getRole());

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.UPDATE_SUCCESS.getStatus(),
                        "비밀번호 변경 완료",
                        ResponseEntity.noContent().build()
                )
        );
    }

    // 계좌 해지
    @DeleteMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "입출금 계좌 해지", description = "계좌를 해지 API 입니다.")
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId) {

        accountService.deleteAccount(accountId, userDetails.getUserId(), userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.DELETE_SUCCESS.getStatus(),
                        "계좌 해지 완료",
                        ResponseEntity.noContent().build()
                )
        );
    }

    // 대출 계좌 해지
    @DeleteMapping("/{account_id}/loan")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "대출 계좌 해지", description = "대출 계좌를 해지 API 입니다.")
    public ResponseEntity<?> deleteLoanAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId) {

        accountService.deleteLoanAccount(accountId, userDetails.getUserId(), userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.DELETE_SUCCESS.getStatus(),
                        "계좌 해지 완료",
                        ResponseEntity.noContent().build()
                )
        );
    }

    // 계좌 전체 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    @Operation(summary = "계좌 전체 조회", description = "계좌 전체 조회 API 입니다.")
    public ResponseEntity<?> getAccounts(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody AccountSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "계좌 전체 조회 완료",
                        accountService.getAccounts(search, pageable)
                )
        );
    }

    // 계좌 상세 조회
    @GetMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "계좌 상세 조회", description = "계좌 상세 조회 API 입니다.")
    public ResponseEntity<?> getAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "계좌 상세 조회 완료",
                        accountService.getAccount(accountId, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

}
