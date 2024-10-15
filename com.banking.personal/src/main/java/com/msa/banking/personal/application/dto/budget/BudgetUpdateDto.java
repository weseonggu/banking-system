package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetUpdateDto {

    private BudgetPeriod period;
    private BigDecimal totalBudget;
}
