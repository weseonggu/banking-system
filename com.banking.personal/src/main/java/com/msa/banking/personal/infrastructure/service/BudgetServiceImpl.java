package com.msa.banking.personal.infrastructure.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.budget.BudgetListDto;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetResponseDto;
import com.msa.banking.personal.application.dto.budget.BudgetUpdateDto;
import com.msa.banking.personal.application.service.BudgetService;
import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.infrastructure.repository.BudgetRepository;
import com.msa.banking.personal.infrastructure.repository.PersonalHistoryRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final PersonalHistoryRepository personalHistoryRepository;
    private final CacheManager cacheManager;

    private static final int MAX_BUDGET_LIMIT = 20;


    /**
     * 예산 설정 목록 조회
     */
    @Override
    @Cacheable(cacheNames = "budgetListCache")
    public Page<BudgetListDto> getBudgetList(Pageable pageable, UUID userId, String userRole) {

        if(userRole.equals("CUSTOMER")){
            Page<Budget> budgetPage = budgetRepository.findAllByUserId(userId, pageable);
            return budgetPage.map(BudgetListDto::toDTO);
        }
        Page<Budget> budgetPage = budgetRepository.findAllByIsDeleteFalse(pageable);
        return budgetPage.map(BudgetListDto::toDTO);
    }

    /**
     * 예산 설정 단건 조회
     */
    @Override
    @Cacheable(cacheNames = "budgetCache", key = "#budgetId")
    public BudgetResponseDto findBudgetById(UUID budgetId, String userRole, UUID userId) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.BUDGET_NOT_FOUND));

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, budget.getUserId());
        }

        return BudgetResponseDto.toDTO(budget);
    }

    /**
     * 예산 설정 생성
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "budgetCache", key = "#result.budgetId")
    public BudgetResponseDto createBudget(BudgetRequestDto budgetRequestDto, String userRole, UUID userId, String userName) {

        if(budgetRequestDto.getPeriod() == null ){
            throw new IllegalArgumentException("WEEKLY, MONTHLY 둘 중 기간을 정해주세요.");
        }

        LocalDateTime startDate = budgetRequestDto.getStartDate();
        LocalDateTime endDate = Budget.calculateEndDate(startDate, budgetRequestDto.getPeriod());

        Long budgetCount = budgetRepository.countBudgetByUserId(userId);

        if(budgetCount < MAX_BUDGET_LIMIT){
            Budget budget = Budget.createBudget(budgetRequestDto, userId, userName);

            // Optional을 사용하여 금액이 있을 경우에만 예산에 추가
            Optional<BigDecimal> totalSpentAmountOpt = personalHistoryRepository
                    .findTotalAmountByDateRange(userId, startDate, endDate);

            totalSpentAmountOpt.ifPresent(budget::addTransactionAmount);

            budgetRepository.save(budget);
            return BudgetResponseDto.toDTO(budget);

        } else {
            throw new GlobalCustomException(ErrorCode.BUDGET_LIMIT_EXCEEDED);
        }
    }


    /**
     * 예산 설정 수정
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "budgetCache", key = "#budgetId")
    @CacheEvict(cacheNames = "budgetListCache", allEntries = true)
    public BudgetResponseDto updateBudget(UUID budgetId, BudgetUpdateDto budgetUpdateDto, String userRole, UUID userId, String userName) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.BUDGET_NOT_FOUND));

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, budget.getUserId());
        }

        budget.updateBudget(budgetUpdateDto, userName);
        budgetRepository.save(budget);

        return BudgetResponseDto.toDTO(budget);
    }

    /**
     * 예산 설정 삭제(Soft Delete)
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "budgetCache", key = "#budgetId")
    public void deleteBudget(UUID budgetId, String userRole, UUID userId, String userName) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.BUDGET_NOT_FOUND));

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, budget.getUserId());
        }

        budget.deleteBudget(userName);
        budgetRepository.save(budget);

        // 전체 조회 캐시 삭제 (리스트 캐시)
        Cache budgetListCache = cacheManager.getCache("budgetListCache");
        if (budgetListCache != null) {
            budgetListCache.clear();
        }
    }

    // 고객일 때, 자신의 개인 내역 검색
    public void checkUserAccess(UUID userId, String userRole, UUID budgetUserId) {
        if (userRole.equals("CUSTOMER") && !userId.equals(budgetUserId)) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }
    }
}
