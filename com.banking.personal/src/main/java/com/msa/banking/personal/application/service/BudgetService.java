package com.msa.banking.personal.application.service;

import com.msa.banking.personal.application.dto.budget.BudgetListDto;
import com.msa.banking.personal.application.dto.budget.BudgetRequestDto;
import com.msa.banking.personal.application.dto.budget.BudgetResponseDto;
import com.msa.banking.personal.application.dto.budget.BudgetUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BudgetService {

    // 예산 설정 목록 조회
    Page<BudgetListDto> getBudgetList(Pageable pageable, UUID userId, String userRole);

    // 예산 설정 단 건 조회
    BudgetResponseDto findBudgetById(UUID budgetId, String userRole, UUID userId);

    // 예산 설정 생성
    BudgetResponseDto createBudget(BudgetRequestDto budgetRequestDto, String userRole, UUID userId, String userName);

    // 예산 설정 수정
    BudgetResponseDto updateBudget(UUID budgetId, BudgetUpdateDto budgetUpdateDto, String userRole, UUID userId, String userName);

    // 예산 설정 삭제
    void deleteBudget(UUID budgetId, String userRole, UUID userId, String userName);
}
