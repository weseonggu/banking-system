package com.msa.banking.account.presentation.dto.transactions;


import com.msa.banking.common.account.type.TransactionType;

import java.math.BigDecimal;

public record WithdrawalTransactionRequestDto(

        TransactionType type,
        BigDecimal withdrawalAmount,
        String description,
        String accountPin
) {
}
