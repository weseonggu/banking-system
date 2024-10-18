package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferTransactionRequestDto{

    @NotBlank(message = "거래 분류는 필수 입력 사항입니다.")
    private TransactionType type;

    @NotBlank(message = "이체 금액은 필수 입력 사항입니다.")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "계좌 번호는 필수 입력 사항입니다.")
    private String beneficiaryAccount;

    @NotBlank(message = "계좌 비밀 번호는 필수 입력 사항입니다.")
    private String accountPin;
}
