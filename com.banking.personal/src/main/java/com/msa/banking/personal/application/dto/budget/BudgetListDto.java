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
public class BudgetListDto {

    private UUID budgetId;
    private BudgetPeriod period;
    private BigDecimal totalBudget;
    private BigDecimal spentAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static BudgetListDto toDTO(Budget budget){
        return BudgetListDto.builder()
                .budgetId(budget.getId())
                .period(budget.getPeriod())
                .totalBudget(budget.getTotalBudget())
                .spentAmount(budget.getSpentAmount())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .build();
    }
}
