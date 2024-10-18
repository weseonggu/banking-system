package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepositTransactionRequestDto{

    @NotBlank(message = "계좌 번호는 필수 입력 사항입니다.")
    private String accountNumber;

    @NotBlank(message = "거래 분류는 필수 입력 사항입니다.")
    private TransactionType type;

    @NotBlank(message = "입금액는 필수 입력 사항입니다.")
    private BigDecimal depositAmount;

    private String description;
}
