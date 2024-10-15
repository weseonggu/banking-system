package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;

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
        String accountPin,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy,
        Boolean isDelete
) {
}