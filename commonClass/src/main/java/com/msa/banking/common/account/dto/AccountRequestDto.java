package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.AccountType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto{

    @NotBlank(message = "계좌 소유주명은 필수 입력 사항입니다.")
    private String accountHolder;

    //TODO: requestDto에서 생략. 상품 가입 시 서비스 비즈니스 로직으로 처리.

    @NotBlank(message = "계좌 분류는 필수 입력 사항입니다.")
    private AccountType type;
    @NotBlank(message = "계좌 비밀번호는 필수 입력 사항입니다.")
    private String accountPin;

    //TODO: 계좌 비밀 번호 재입력 체크
    @NotBlank(message = "계좌 비밀번호 재입력은 필수 입력 사항입니다.")
    private String checkPin;
}
