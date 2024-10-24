package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferTransactionRequestDto{

    @NotNull(message = "거래 분류는 필수 입력 사항입니다.")
    private TransactionType type;

    @NotNull(message = "이체 금액은 필수 입력 사항입니다.")
    @DecimalMin(value = "0.01", message = "이체 금액은 0보다 커야 합니다.")
    @DecimalMax(value = "100000000.00", message = "이체 금액은 최대 100,000,000원을 넘을 수 없습니다.")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "계좌 번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호를 xxx-xxxx-xxxxxxx 형식에 맞게 입력해주세요.")
    private String beneficiaryAccount;

    @NotBlank(message = "계좌 비밀 번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{6}", message = "계좌 비밀번호는 숫자로만 이루어진 6자리여야 합니다.")
    private String accountPin;
}
