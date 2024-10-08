package com.msa.banking.account.presentation.dto.directDebit;

import com.msa.banking.account.domain.model.DirectDebitStatus;

import java.time.LocalDateTime;

public record DirectDebitListResponseDto(

        String originatingAccount,
        String beneficiaryAccount,
        DirectDebitStatus status,
        LocalDateTime createdAt,
        String createdBy
) {
}
