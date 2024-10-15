package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionType;

import java.math.BigDecimal;

public record TransferTransactionRequestDto(

        TransactionType type,
        BigDecimal amount,
        String description,
        String beneficiaryAccount,
        String accountPin
) {
}
