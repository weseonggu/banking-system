package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;

public record AccountRequestDto(

        String accountHolder,
        AccountStatus status,
        AccountType type,
        String accountPin
) {
}
