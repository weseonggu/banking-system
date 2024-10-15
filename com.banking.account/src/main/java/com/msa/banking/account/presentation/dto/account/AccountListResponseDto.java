package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;

import java.time.LocalDateTime;


public record AccountListResponseDto(

        String accountNumber,
        String accountHolder,
        AccountStatus status,
        AccountType type,
        LocalDateTime createdAt,
        String createdBy
) {
}
