package com.msa.banking.account.presentation.dto;

import com.msa.banking.account.domain.model.DirectDebitStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DirectDebitResponseDto(

        UUID directDebitId,
        UUID accountId,
        String beneficiaryAccount,
        BigDecimal amount,
        Integer transferDate,
        DirectDebitStatus status,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy,
        Boolean isDelete
) {
}
