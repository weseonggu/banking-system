package com.msa.banking.personal.infrastructure.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.category.MostSpentCategoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryListDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryRequestDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryResponseDto;
import com.msa.banking.personal.application.dto.personalHistory.PersonalHistoryUpdateDto;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.service.BudgetService;
import com.msa.banking.personal.application.service.CategoryService;
import com.msa.banking.personal.application.service.PersonalHistoryService;
import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.infrastructure.repository.PersonalHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PersonalHistoryServiceImpl implements PersonalHistoryService {

    private final PersonalHistoryRepository personalHistoryRepository;
    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final CacheManager cacheManager;

    /**
     * 개인 내역 목록 조회
     * 조건: 카테고리 이름, 상태
     */
    @Override
    @Cacheable(cacheNames = "personalHistoryListCache")
    public Page<PersonalHistoryListDto> searchPersonalHistory(String categoryName, PersonalHistoryStatus status, Pageable pageable, UUID userId, String userRole) {

        if (userRole.equals("CUSTOMER")) {
            Page<PersonalHistory> personalHistoryPage = personalHistoryRepository.findByCategoryAndStatus(categoryName, status, userId, pageable);
            return personalHistoryPage.map(PersonalHistoryListDto::toDTO);
        }
        Page<PersonalHistory> personalHistoryPage = personalHistoryRepository.findByCategoryAndStatus(categoryName, status, pageable);
        return personalHistoryPage.map(PersonalHistoryListDto::toDTO);
    }

    /**
     * 개인 내역 생성 (카테고리 미분류)
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#result.historyId")
    public PersonalHistoryResponseDto createPersonalHistory(AccountCompletedEventDto accountCompletedEventDto) {

        // 계좌에서 거래가 일어났을 때 데이터를 받아서 개인 내역에 저장
        PersonalHistory personalHistory = PersonalHistory.createPersonalHistory(accountCompletedEventDto);
        PersonalHistory savePersonalHistory = personalHistoryRepository.save(personalHistory);

        UUID userId = savePersonalHistory.getUserId();
        LocalDateTime transactionDate = savePersonalHistory.getTransactionDate();
        BigDecimal transactionAmount = savePersonalHistory.getAmount();

        // 해당 기간에 속하는 모든 예산 설정을 조회
        List<Budget> budgets = budgetService.findAllByUserIdAndPeriod(userId,transactionDate);

        // 예산 설정이 존재하면 처리
        if (!budgets.isEmpty()) {
            budgets.forEach(budget -> {
                // 각 예산에 대해 금액을 추가
                budgetService.addTransactionAmountToBudget(budget,transactionAmount,personalHistory);
            });
        }
        return PersonalHistoryResponseDto.toDTO(savePersonalHistory);
    }

    /**
     * 개인 내역 단 건 조회
     */
    @Override
    @Cacheable(cacheNames = "personalHistoryCache", key = "#historyId")
    public PersonalHistoryResponseDto findPersonalHistoryById(Long historyId, UUID userId, String userRole) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if (userRole.equals("CUSTOMER")) {
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 조회 후 카테고리 업데이트
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#historyId")
    @CacheEvict(cacheNames = "personalHistoryListCache", allEntries = true)
    public PersonalHistoryResponseDto updatePersonalHistoryCategory(PersonalHistoryUpdateDto personalHistoryUpdateDto, Long historyId, UUID userId, String userRole, String userName) {

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if (userRole.equals("CUSTOMER")) {
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        // 입력받은 카테고리 이름 조회 후 존재하지 않으면 생성 후 저장 -> 카테고리 업데이트
        Optional<Category> optionalCategory = categoryService.findByName(personalHistoryUpdateDto.getCategoryName(), userId);

        if (optionalCategory.isPresent()) {
            personalHistory.updateCategory(optionalCategory.get(), userName);
            personalHistoryRepository.save(personalHistory);
        } else {
            Category category = categoryService.createCategory(personalHistoryUpdateDto.getCategoryName(),userId);
            personalHistory.updateCategory(category, userName);
            personalHistoryRepository.save(personalHistory);
        }

        return PersonalHistoryResponseDto.toDTO(personalHistory);
    }

    /**
     * 개인 내역 삭제(Soft Delete)
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "personalHistoryCache", key = "#historyId")
    public void deletePersonHistory(Long historyId, UUID userId, String userRole, String userName) {

        log.info("deletePersonHistory Service-------------");

        PersonalHistory personalHistory = personalHistoryRepository.findById(historyId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.PERSONAL_HISTORY_NOT_FOUND)
        );

        if (userRole.equals("CUSTOMER")) {
            checkUserAccess(userId, userRole, personalHistory.getUserId());
        }

        personalHistory.deletePersonalHistory(userName);
        personalHistoryRepository.save(personalHistory);

        // 전체 조회 캐시 삭제 (리스트 캐시)
        Cache personalHistoryListCache = cacheManager.getCache("personalHistoryListCache");
        if (personalHistoryListCache != null) {
            personalHistoryListCache.clear();
        }
    }

    // 고객일 때, 자신의 개인 내역 검색
    public void checkUserAccess(UUID userId, String userRole, UUID historyUserId) {
        if (userRole.equals("CUSTOMER") && !userId.equals(historyUserId)) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }
    }

    /**
     * 개인 내역 생성 (카테고리 미분류) - (카프카 알림 테스트용) TODO Account - Kafka 개발 완료 시 삭제
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "personalHistoryCache", key = "#result.historyId")
    public PersonalHistoryResponseDto createPersonalHistory(PersonalHistoryRequestDto requestDto, UUID userId, String userName) {

        // 계좌에서 거래가 일어났을 때 데이터를 받아 개인 내역에 저장
        PersonalHistory personalHistory = PersonalHistory.createPersonalHistory(requestDto, userId, userName);
        PersonalHistory savePersonalHistory = personalHistoryRepository.save(personalHistory);

        LocalDateTime transactionDate = savePersonalHistory.getTransactionDate();
        BigDecimal transactionAmount = savePersonalHistory.getAmount();

        // 해당 기간에 속하는 모든 예산 설정을 조회
        List<Budget> budgets = budgetService.findAllByUserIdAndPeriod(userId, transactionDate);

        // 예산 설정이 존재하면 처리
        if (!budgets.isEmpty()) {
            budgets.forEach(budget -> {
                // 각 예산에 대해 금액을 추가
                budgetService.addTransactionAmountToBudget(budget,transactionAmount,personalHistory);
            });
        }
        return PersonalHistoryResponseDto.toDTO(savePersonalHistory);
    }

    /**
     * 설정한 기간 내 가장 많은 금액을 소비한 카테고리, 총 소비 금액
     */
    @Override
    public MostSpentCategoryResponseDto findMostSpentCategory(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {

        List<Object[]> result = personalHistoryRepository.findMostSpentCategoryByUserIdAndDateRange(userId,startDate,endDate);

        if(!result.isEmpty()){
            Object[] resultArray = result.get(0);
            return MostSpentCategoryResponseDto.builder()
                    .categoryName((String) resultArray[0])
                    .totalSpent((BigDecimal) resultArray[1])
                    .build();
        } else {
            throw new GlobalCustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

}
