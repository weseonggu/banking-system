package com.msa.banking.account.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FirstBatchAccountResponseDto {

    private UUID accountId;
    private BigDecimal balance;
}
