package com.msa.banking.personal.application.service;

import com.msa.banking.personal.application.dto.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PersonalHistoryService {

    // 개인 내역 목록 조회
    Page<PersonalHistoryListDto> searchPersonalHistory(String categoryName, Boolean status, Pageable pageable);

    // 개인 내역 생성
    PersonalHistoryResponseDto createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto);

    // 개인 내역 단건 조회
    PersonalHistoryResponseDto findPersonalHistoryById(Long historyId);

    // 개인 내역 수정(카테고리 수정)
    PersonalHistoryResponseDto updatePersonalHistoryCategory(PersonalHistoryUpdateDto personalHistoryUpdateDto, Long historyId);

    // 개인 내역 삭제
    void deletePersonHistory(Long historyId);
}
