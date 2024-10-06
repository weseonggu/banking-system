package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionStatus;
import com.msa.banking.account.domain.model.TransactionType;

import java.time.LocalDateTime;

public record TransactionsListResponseDto(

        TransactionType type,
        TransactionStatus status,
        String originatingAccount,
        String beneficiaryAccount,
        LocalDateTime createdAt,
        String createdBy
) {
}
