package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetRequestDto {

    private BudgetPeriod period;
    private BigDecimal totalBudget;
    private LocalDateTime startDate;

}
