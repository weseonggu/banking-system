package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;

public record SingleTransactionRequestDto(

        TransactionType type,
        BigDecimal amount,
        String description,
        String accountPin
) {
}
