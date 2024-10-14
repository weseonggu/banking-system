package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.DirectDebitService;
import com.msa.banking.account.domain.model.DirectDebitStatus;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitListResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitRequestDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitResponseDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitSearchRequestDto;
import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
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
@RequestMapping("/api/direct-debits")
public class DirectDebitController {

    private final DirectDebitService directDebitService;

    public DirectDebitController(DirectDebitService directDebitService) {
        this.directDebitService = directDebitService;
    }


    // 자동 이체 등록
    @PostMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> createDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody DirectDebitRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "자동 이체 등록 완료",
                        directDebitService.createDirectDebit(accountId, request,userDetails.getUsername(), userDetails.getRole())
                )
        );
    }

    // 자동 이체 수정
    @PatchMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> updateDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId,
            @RequestParam DirectDebitStatus status,
            @RequestBody DirectDebitRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.UPDATE_SUCCESS.getStatus(),
                        "계좌 상태 변경 완료",
                        directDebitService.updateDirectDebit(directDebitId, status, request, userDetails.getUsername(), userDetails.getRole())
                )
        );
    }

    // 자동 이체 해지
    @DeleteMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> deleteDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId) {

        directDebitService.deleteDirectDebit(directDebitId, userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.DELETE_SUCCESS.getStatus(),
                        "자동 이체 해지 완료",
                        ResponseEntity.noContent().build()
                )
        );
    }

    // 자동 이체 전체 조회
    // TODO: QueryDSL 구현체 작성.
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER')")
    public ResponseEntity<?> getDirectDebits(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody DirectDebitSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "계좌 전체 조회 완료",
                        directDebitService.getDirectDebits(search, pageable)
                )
        );
    }

    // 자동 이체 상세 조회
    @GetMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "계좌 상세 조회 완료",
                        directDebitService.getDirectDebit(directDebitId, userDetails.getUsername(), userDetails.getRole())
                )
        );
    }
}