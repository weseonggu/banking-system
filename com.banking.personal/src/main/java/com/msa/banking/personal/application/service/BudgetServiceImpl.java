package com.msa.banking.personal.application.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.budget.BudgetListDto;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetResponseDto;
import com.msa.banking.personal.application.dto.budget.BudgetUpdateDto;
import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    /**
     * 예산 설정 목록 조회
     */
    @Override
    public Page<BudgetListDto> getBudgetList(Pageable pageable) {

        Page<Budget> budgetPage = budgetRepository.findAllByIsDeleteFalse(pageable);
        return budgetPage.map(BudgetListDto::toDTO);
    }

    /**
     * 예산 설정 단건 조회
     */
    @Override
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
    public BudgetResponseDto createBudget(BudgetRequestDto budgetRequestDto, String userRole, UUID userId, String userName) {

        Budget budget = Budget.createBudget(budgetRequestDto, userId, userName);
        budgetRepository.save(budget);

        return BudgetResponseDto.toDTO(budget);
    }


    /**
     * 예산 설정 수정
     */
    @Override
    @Transactional
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
    public void deleteBudget(UUID budgetId, String userRole, UUID userId, String userName) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.BUDGET_NOT_FOUND));

        if(userRole.equals("CUSTOMER")){
            checkUserAccess(userId, userRole, budget.getUserId());
        }

        budget.deleteBudget(userName);
        budgetRepository.save(budget);
    }

    // TODO 개인 내역이 추가 될 떄 예산쪽에서도 총 사용가격 실시간 반영 후 예산을 넘기면 슬랙 알림

    // 고객일 때, 자신의 개인 내역 검색
    public void checkUserAccess(UUID userId, String userRole, UUID budgetUserId) {
        if (userRole.equals("CUSTOMER") && !userId.equals(budgetUserId)) {
            throw new GlobalCustomException(ErrorCode.USER_FORBIDDEN);
        }
    }
}
