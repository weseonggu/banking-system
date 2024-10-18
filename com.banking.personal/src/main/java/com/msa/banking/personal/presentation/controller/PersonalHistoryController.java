package com.msa.banking.personal.presentation.controller;

import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.personal.application.dto.category.MostSpentCategoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.application.service.PersonalHistoryService;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal-histories")
@Log4j2(topic = "PersonalHistoryController")
@Tag(name = "개인 내역 서비스", description = "개인 내역에 관한 API 입니다.")
public class PersonalHistoryController {

    private final PersonalHistoryService personalHistoryService;

    // 개인 내역 전체 조회
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "개인 내역 목록 조회", description = "개인 내역 목록 조회 API 입니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> searchPersonalHistory(@RequestParam(value = "categoryName", required = false) String categoryName,
                                                   @RequestParam(value = "status", required = false) PersonalHistoryStatus status,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   Pageable pageable) {

        UUID userId = userDetails.getUserId();
        String userRole = userDetails.getRole();
        Page<PersonalHistoryListDto> personalHistoryListPage = personalHistoryService.searchPersonalHistory(categoryName, status, pageable, userId, userRole);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "searchPersonalHistory", personalHistoryListPage));
    }

    // 개인 내역 단 건 조회
    @GetMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    @Operation(summary = "개인 내역 단 건 조회", description = "개인 내역 단 건 조회 API 입니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> findPersonalHistoryById(@PathVariable("history_id") Long historyId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();

        PersonalHistoryResponseDto responseDto = personalHistoryService.findPersonalHistoryById(historyId, userId, role);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "findPersonalHistoryById", responseDto));

    }

    // 개인 내역 수정(카테고리 수정)
    @PatchMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    @Operation(summary = "개인 내역 수정(카테고리 변경)", description = "미분류된 카테고리를 수정하는 API 입니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updatePersonalHistoryCategory(@PathVariable("history_id") Long historyId, @Valid @RequestBody PersonalHistoryUpdateDto personalHistoryUpdateDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();
        String userName = userDetails.getUsername();

        PersonalHistoryResponseDto responseDto = personalHistoryService.updatePersonalHistoryCategory(personalHistoryUpdateDto, historyId, userId, role, userName);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "updatePersonalHistoryCategory", responseDto));
    }

    // 개인 내역 삭제(Soft Delete)
    @DeleteMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    @Operation(summary = "개인 내역 삭제", description = "개인 내역 삭제 API 입니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deletePersonalHistory(@PathVariable("history_id") Long historyId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID userId = userDetails.getUserId();
        String role = userDetails.getRole();
        String userName = userDetails.getUsername();

        personalHistoryService.deletePersonHistory(historyId, userId, role,userName);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.DELETE_SUCCESS.getStatus(), "updatePersonalHistoryCategory", "개인 내역 삭제되었습니다."));
    }

    /**
     * 개인 내역 생성 (카테고리 미분류) -(카프카 알림 테스트용) TODO Account - Kafka 개발 완료 시 삭제
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    @Operation(summary = "개인 내역 생성", description = "개인 내역 생성 - 테스트용 (원래는 계좌에서 거래가 일어나면 자동 생성됨)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createPersonalHistory(@RequestBody PersonalHistoryRequestDto requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails){

        UUID userId = userDetails.getUserId();
        String userName = userDetails.getUsername();

        PersonalHistoryResponseDto responseDto = personalHistoryService.createPersonalHistory(requestDto,userId,userName);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.INSERT_SUCCESS.getStatus(), "createPersonalHistory", responseDto));
    }

    /**
     * 설정한 기간 내 가장 많은 금액을 소비한 카테고리, 총 소비 금액
     */
    @GetMapping("/most-spent")
    @Operation(summary = "가장 많은 금액을 소비한 카테고리, 금액 조회", description = "설정한 기간 내 가장 많은 금액을 소비한 카테고리, 총 소비 금액 조회 API 입니다.")
    public ResponseEntity<?> findMostSpentCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestParam("startDate")LocalDateTime startDate,
                                                   @RequestParam("endDate") LocalDateTime endDate){
        UUID userId = userDetails.getUserId();

        MostSpentCategoryResponseDto responseDto = personalHistoryService.findMostSpentCategory(userId,startDate,endDate);

        String formattedResponse = String.format("가장 큰 소비 카테고리는 %s, 총 소비 금액은 %s원 입니다.",
                responseDto.getCategoryName(),
                responseDto.getTotalSpent().toString());

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "findMostSpentCategory", formattedResponse));

    }
}
