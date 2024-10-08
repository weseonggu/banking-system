package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.account.domain.model.AccountStatus;
import com.msa.banking.account.domain.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSearchRequestDto {

    private String accountNumber;
    private String accountHolder;
    private AccountType type;
}
