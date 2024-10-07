package com.msa.banking.performance.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceRequestDto {

    private BigDecimal totalTransactionAmount;
    private Long loanCount;
    private Long depositCount;
    private Year evaluationYear;
    private YearMonth evaluationMonth;
}
