package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.common.account.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSearchRequestDto {

    private String accountNumber;
    private String accountHolder;
    private AccountType type;
}
