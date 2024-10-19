package com.msa.banking.account.presentation.dto.account;

import com.msa.banking.common.account.type.AccountType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSearchRequestDto {

    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호를 xxx-xxxx-xxxxxxx 형식에 맞게 입력해주세요.")
    private String accountNumber;

    private String accountHolder;
    private AccountType type;
}