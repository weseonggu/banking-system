package com.msa.banking.account.presentation.dto.directDebit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class DirectDebitSearchRequestDto {

    private String originatingAccount;
    private String beneficiaryAccount;
}
