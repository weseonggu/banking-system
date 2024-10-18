package com.msa.banking.personal.application.dto.budget;

import com.msa.banking.personal.domain.enums.BudgetPeriod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetRequestDto {

    private BudgetPeriod period;

    @NotNull(message = "금액은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "금액은 0보다 커야 합니다.")
    private BigDecimal totalBudget;

    @NotNull(message = "날짜는 필수 입력 항목입니다.")
    private LocalDateTime startDate;

}
