package com.msa.banking.account.presentation.dto;

import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;

public record TransactionsRequestDto(

        TransactionType type,
        BigDecimal amount,
        String description,
        String originatingAccount,
        String beneficiaryAccount
) {
}
