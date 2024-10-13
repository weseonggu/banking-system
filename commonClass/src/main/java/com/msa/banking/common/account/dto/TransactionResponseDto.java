package com.msa.banking.common.account.dto;

import com.msa.banking.account.domain.model.TransactionStatus;
import com.msa.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(

        Long transactionId,
        UUID accountId,
        TransactionType type,
        BigDecimal depositAmount,
        BigDecimal withdrawalAmount,
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
