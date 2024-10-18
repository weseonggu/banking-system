package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto{

    @NotBlank(message = "계좌 소유주명은 필수 입력 사항입니다.")
    private String accountHolder;

    @NotBlank(message = "계좌 분류는 필수 입력 사항입니다.")
    private AccountType type;

    @NotBlank(message = "계좌 비밀번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String accountPin;

    @NotBlank(message = "계좌 비밀번호 재입력은 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String checkAccountPin;
}
