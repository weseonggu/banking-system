package com.msa.banking.personal.presentation.controller;

import com.msa.banking.common.response.SuccessCode;
import com.msa.banking.common.response.SuccessResponse;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.application.service.PersonalHistoryService;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal-histories")
@Log4j2(topic = "PersonalHistoryController")
public class PersonalHistoryController {

    private final PersonalHistoryService personalHistoryService;

    // 개인 내역 전체 조회
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> searchPersonalHistory(@RequestParam(value = "categoryName", required = false) String categoryName,
                                                   @RequestParam(value = "status", required = false) PersonalHistoryStatus status,
                                                   Pageable pageable) {
        Page<PersonalHistoryListDto> personalHistoryListPage = personalHistoryService.searchPersonalHistory(categoryName, status, pageable);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "searchPersonalHistory", personalHistoryListPage));
    }

    // 개인 내역 단건 조회
    @GetMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> findPersonalHistoryById(@PathVariable("history_id") Long historyId) {

        PersonalHistoryResponseDto responseDto = personalHistoryService.findPersonalHistoryById(historyId);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.SELECT_SUCCESS.getStatus(), "findPersonalHistoryById", responseDto));

    }

    // 개인 내역 수정(카테고리 수정)
    @PatchMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    public ResponseEntity<?> updatePersonalHistoryCategory(@PathVariable("history_id") Long historyId, @RequestBody PersonalHistoryUpdateDto personalHistoryUpdateDto) {

        PersonalHistoryResponseDto responseDto = personalHistoryService.updatePersonalHistoryCategory(personalHistoryUpdateDto, historyId);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.UPDATE_SUCCESS.getStatus(), "updatePersonalHistoryCategory", responseDto));
    }

    // 개인 내역 삭제(Soft Delete)
    @DeleteMapping("/{history_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'CUSTOMER')")
    public ResponseEntity<?> deletePersonalHistory(@PathVariable("history_id") Long historyId) {

        personalHistoryService.deletePersonHistory(historyId);

        return ResponseEntity.ok(
                new SuccessResponse<>(SuccessCode.DELETE_SUCCESS.getStatus(), "updatePersonalHistoryCategory", null));
    }

}
