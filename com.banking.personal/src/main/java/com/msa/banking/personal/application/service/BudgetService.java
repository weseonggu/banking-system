package com.msa.banking.personal.application.service;

import com.msa.banking.personal.application.dto.budget.BudgetListDto;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BudgetService {

    // 예산 설정 목록 조회
    Page<BudgetListDto> getBudgetList(Pageable pageable);

    // 예산 설정 단건 조회
    BudgetResponseDto createBudget(BudgetRequestDto budgetRequestDto);

    // 예산 설정 생성
    BudgetResponseDto findBudgetById(UUID budgetId);

    // 예산 설정 수정
    BudgetResponseDto updateBudget(BudgetRequestDto budgetRequestDto);

    // 예산 설정 삭제
    void deleteBudget(UUID budgetId);
}
