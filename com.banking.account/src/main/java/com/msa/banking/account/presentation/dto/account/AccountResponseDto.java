package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.account.domain.model.AccountStatus;
import com.msa.banking.account.domain.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponseDto(

        UUID accountId,
        String accountNumber,
        String accountHolder,
        BigDecimal balance,
        AccountStatus status,
        AccountType type,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy,
        Boolean isDelete
) {
}