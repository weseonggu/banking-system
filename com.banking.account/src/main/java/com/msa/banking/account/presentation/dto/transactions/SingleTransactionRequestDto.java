package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionType;

import java.math.BigDecimal;

public record SingleTransactionRequestDto(

        TransactionType type,
        BigDecimal amount,
        String description,
        String accountPin
) {
}
