package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionStatus;
import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionsResponseDto(

        Long transactionId,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        TransactionStatus status,
        String description,
        String originatingAccount,
        String beneficiaryAccount,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy,
        Boolean isDelete
) {
}
