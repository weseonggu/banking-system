package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;

public record DepositTransactionRequestDto(

        TransactionType type,
        BigDecimal depositAmount,
        String description,
        String accountPin
) {
}
