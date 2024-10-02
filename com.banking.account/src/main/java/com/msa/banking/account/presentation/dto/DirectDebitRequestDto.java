package com.msa.banking.account.presentation.dto;

import java.math.BigDecimal;

public record DirectDebitRequestDto(

        String beneficiaryAccount,
        BigDecimal amount,
        Integer transferDate
) {
}
