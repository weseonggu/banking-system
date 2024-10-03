package com.msa.banking.personal.application.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.CategoryRepository;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PersonalHistoryServiceImpl implements PersonalHistoryService{

    private final PersonalHistoryRepository personalHistoryRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 개인 내역 목록 조회
     * 조건: 사용자 ID, 카테고리 이름, 상태
     */
    @Override
    public Page<PersonalHistoryListDto> searchPersonalHistory(String categoryName, PersonalHistoryStatus status, Pageable pageable) {
        Page<PersonalHistory> personalHistoryPage = personalHistoryRepository.findByCategoryAndStatus(categoryName, status, pageable);
        return personalHistoryPage.map(PersonalHistoryListDto::toDTO);
    }

    /**
     * 개인 내역 생성 (카테고리 미분류)
     */
    @Override
    public PersonalHistoryResponseDto createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto) {

        // 계좌에서 거래가 일어났을 때 데이터를 받아서 개인 내역에 저장
        PersonalHistory personalHistory = PersonalHistory.createPersonalHistory(accountCompletedEventDto);
        PersonalHistory savePersonalHistory = personalHistoryRepository.save(personalHistory);

        return PersonalHistoryResponseDto.toDTO(savePersonalHistory);
    }

    /**
     * 개인 내역 단건 조회
     */
    @Override
    public PersonalHistoryResponseDto findPersonalHistoryById(Long historyId) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                ()-> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 조회 후 카테고리 업데이트
     */
    @Override
    public PersonalHistoryResponseDto updatePersonalHistoryCategory(PersonalHistoryUpdateDto personalHistoryUpdateDto, Long historyId) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                ()-> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );
        
        // 입력받은 카테고리 이름 조회 후 존재하지 않으면 생성 후 저장 -> 카테고리 업데이트
        Optional<Category> optionalCategory = categoryRepository.findByName(personalHistoryUpdateDto.getCategoryName());

        if (optionalCategory.isPresent()) {
            personalHistory.updateCategory(optionalCategory.get());
            personalHistoryRepository.save(personalHistory);
        } else {
            Category category = Category.createCategory(personalHistoryUpdateDto.getCategoryName());
            categoryRepository.save(category);
            personalHistory.updateCategory(category);
            personalHistoryRepository.save(personalHistory);
        }

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 삭제(Soft Delete)
     */
    @Override
    public void deletePersonHistory(Long historyId) {

        log.info("deletePersonHistory Service-------------");

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                ()-> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );
        personalHistory.deletePersonalHistory();
        personalHistoryRepository.save(personalHistory);
    }
}
