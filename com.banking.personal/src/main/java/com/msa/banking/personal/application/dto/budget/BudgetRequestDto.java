package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetRequestDto {

    private UUID userId;
    private BudgetPeriod period;
    private BigDecimal totalBudget;
    private LocalDateTime startDate;

}
