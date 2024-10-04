package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import com.msa.banking.personal.domain.model.Budget;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BudgetResponseDto {

    private UUID budgetId;
    private UUID userId;
    private BudgetPeriod period;
    private BigDecimal totalBudget;
    private BigDecimal spentAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static BudgetResponseDto toDTO(Budget budget){
        return BudgetResponseDto.builder()
                .budgetId(budget.getId())
                .userId(budget.getUserId())
                .period(budget.getPeriod())
                .totalBudget(budget.getTotalBudget())
                .spentAmount(budget.getSpentAmount())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .build();
    }

}
