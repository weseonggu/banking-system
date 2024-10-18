package com.msa.banking.account.presentation.dto.directDebit;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class DirectDebitSearchRequestDto {

    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호 형식에 맞게 입력해주세요.")
    private String originatingAccount;

    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호 형식에 맞게 입력해주세요.")
    private String beneficiaryAccount;
}