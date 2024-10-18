package com.msa.banking.personal.application.service;

import com.msa.banking.personal.application.dto.category.MostSpentCategoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PersonalHistoryService {

    // 개인 내역 목록 조회
    Page<PersonalHistoryListDto> searchPersonalHistory(String categoryName, PersonalHistoryStatus status, Pageable pageable, UUID userId, String userRole);

    // 개인 내역 생성
    PersonalHistoryResponseDto createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto);

    // 개인 내역 단 건 조회
    PersonalHistoryResponseDto findPersonalHistoryById(Long historyId, UUID userId, String userRole);

    // 개인 내역 수정(카테고리 수정)
    PersonalHistoryResponseDto updatePersonalHistoryCategory(PersonalHistoryUpdateDto personalHistoryUpdateDto, Long historyId, UUID userId, String userRole, String userName);

    // 개인 내역 삭제
    void deletePersonHistory(Long historyId, UUID userId, String userRole, String userName);

    /**
     * 개인 내역 생성 (카테고리 미분류)-(카프카 알림 테스트용) TODO Account - Kafka 개발 완료 시 삭제
     */
    PersonalHistoryResponseDto createPersonalHistory(PersonalHistoryRequestDto requestDto, UUID userId, String userName);

    // 설정한 기간 내 가장 많은 금액을 소비한 카테고리, 총 소비 금액
    MostSpentCategoryResponseDto findMostSpentCategory(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}
