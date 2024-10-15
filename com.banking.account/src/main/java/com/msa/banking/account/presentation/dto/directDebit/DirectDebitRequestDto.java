package com.msa.banking.account.presentation.dto.directDebit;

import java.math.BigDecimal;

public record DirectDebitRequestDto(

        String beneficiaryAccount,
        BigDecimal amount,
        Integer transferDate
) {
}
