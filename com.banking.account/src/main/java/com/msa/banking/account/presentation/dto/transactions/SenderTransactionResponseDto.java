package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionStatus;
import com.msa.banking.common.account.type.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SenderTransactionResponseDto(

        Long transactionId,
        UUID accountId,
        TransactionType type,
        BigDecimal depositAmount,
        BigDecimal withdrawalAmount,
        TransactionStatus status,
        String description,
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
