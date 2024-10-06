package com.msa.banking.personal.application.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.budget.BudgetListDto;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetResponseDto;
import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Override
    public Page<BudgetListDto> getBudgetList(Pageable pageable) {
        return null;
    }

    @Override
    public BudgetResponseDto createBudget(BudgetRequestDto budgetRequestDto) {

        // TODO 예외 처리

        Budget budget = Budget.createBudget(budgetRequestDto);
        budgetRepository.save(budget);

        return BudgetResponseDto.toDTO(budget);
    }

    @Override
    public BudgetResponseDto findBudgetById(UUID budgetId) {
        
        // TODO 예외 처리

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new GlobalCustomException(ErrorCode.BUDGET_NOT_FOUND));
        return BudgetResponseDto.toDTO(budget);
    }

    @Override
    public BudgetResponseDto updateBudget(BudgetRequestDto budgetRequestDto) {
        return null;
    }

    @Override
    public void deleteBudget(UUID budgetId) {

    }

    // TODO 개인 내역이 추가 될 떄 예산쪽에서도 총 사용가격 실시간 반영 후 예산을 넘기면 슬랙 알림
}
