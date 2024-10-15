package com.msa.banking.account.presentation.controller;

import com.msa.banking.account.application.service.DirectDebitService;
import com.msa.banking.account.domain.model.DirectDebitStatus;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitRequestDto;
import com.msa.banking.account.presentation.dto.directDebit.DirectDebitSearchRequestDto;
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
@RequestMapping("/api/direct-debits")
@Tag(name = "자동 이체 서비스", description = "자동 이체에 관련된 API 입니다.")
public class DirectDebitController {

    private final DirectDebitService directDebitService;

    public DirectDebitController(DirectDebitService directDebitService) {
        this.directDebitService = directDebitService;
    }


    // 자동 이체 등록
    @PostMapping("/{account_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "자동 이체 등록", description = "자동 이체 등록 API 입니다.")
    public ResponseEntity<?> createDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("account_id") UUID accountId,
            @RequestBody DirectDebitRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.INSERT_SUCCESS.getStatus(),
                        "자동 이체 등록 완료",
                        directDebitService.createDirectDebit(accountId, request, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 자동 이체 수정
    @PatchMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "자동 이체 수정", description = "자동 이체 수정 API 입니다.")
    public ResponseEntity<?> updateDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId,
            @RequestParam DirectDebitStatus status,
            @RequestBody DirectDebitRequestDto request) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.UPDATE_SUCCESS.getStatus(),
                        "자동 이체 변경 완료",
                        directDebitService.updateDirectDebit(directDebitId, request, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }

    // 자동 이체 해지
    @DeleteMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "자동 이체 해지", description = "자동 이체 해지 API 입니다.")
    public ResponseEntity<?> deleteDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId) {

        directDebitService.deleteDirectDebit(directDebitId, userDetails.getUserId(), userDetails.getRole());

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
    @Operation(summary = "자동 이체 전체 조회", description = "자동 이체 전체 조회 API 입니다.")
    public ResponseEntity<?> getDirectDebits(
            @RequestParam(defaultValue = "10") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestBody DirectDebitSearchRequestDto search) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "자동 이체 전체 조회 완료",
                        directDebitService.getDirectDebits(search, pageable)
                )
        );
    }

    // 자동 이체 상세 조회
    @GetMapping("/{directDebit_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "자동 이체 상세 조회", description = "자동 이체 상세 조회 API 입니다.")
    public ResponseEntity<?> getDirectDebit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("directDebit_id") UUID directDebitId) {

        return ResponseEntity.ok(
                new SuccessResponse<>(
                        SuccessCode.SELECT_SUCCESS.getStatus(),
                        "자동 이체 상세 조회 완료",
                        directDebitService.getDirectDebit(directDebitId, userDetails.getUserId(), userDetails.getRole())
                )
        );
    }
}