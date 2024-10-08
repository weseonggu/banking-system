package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;

public record TransactionRequestDto(

        TransactionType type,
        BigDecimal amount,
        String description,
        String beneficiaryAccount
) {
}
