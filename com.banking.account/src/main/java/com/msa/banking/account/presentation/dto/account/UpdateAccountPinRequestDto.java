package com.msa.banking.account.presentation.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountPinRequestDto {

    @NotBlank(message = "기존 계좌 비밀번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String originAccountPin;

    @NotBlank(message = "변경할 계좌 비밀번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String changeAccountPin;

    @NotBlank(message = "계좌 비밀번호 재확인은 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String checkAccountPin;
}
