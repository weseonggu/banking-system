package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.account.domain.model.AccountStatus;
import com.msa.banking.account.domain.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
