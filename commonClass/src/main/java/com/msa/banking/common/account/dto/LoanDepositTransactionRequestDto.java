package com.msa.banking.common.account.dto;

import com.msa.banking.common.account.type.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDepositTransactionRequestDto {

    @NotBlank(message = "계좌 번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호를 xxx-xxxx-xxxxxxx 형식에 맞게 입력해주세요.")
    private String accountNumber;

    @NotBlank(message = "거래 분류는 필수 입력 사항입니다.")
    private TransactionType type;

    @NotBlank(message = "입금액는 필수 입력 사항입니다.")
    @DecimalMin(value = "0.01", message = "입금액은 0보다 커야 합니다.")
    @DecimalMax(value = "100000000000", message = "1회 입금액은 100,000,000,000원을 넘을 수 없습니다.")
    private BigDecimal depositAmount;

    private String description;
}
