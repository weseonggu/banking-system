package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetListDto {

    private UUID budgetId;
    private BudgetPeriod period;
    private BigDecimal totalBudget;
    private BigDecimal spentAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
