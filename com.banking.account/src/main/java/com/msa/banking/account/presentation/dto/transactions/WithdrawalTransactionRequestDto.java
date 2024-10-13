package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;

public record WithdrawalTransactionRequestDto(

        TransactionType type,
        BigDecimal withdrawalAmount,
        String description,
        String accountPin
) {
}
