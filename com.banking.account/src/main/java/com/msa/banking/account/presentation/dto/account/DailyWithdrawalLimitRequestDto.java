package com.msa.banking.account.presentation.dto.account;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyWithdrawalLimitRequestDto {

    @NotBlank(message = "하루 출금 한도액은 필수 입력 사항입니다.")
    @DecimalMin(value = "100000.00", message = "하루 출금 한도액은 100,000원보다 커야 합니다.")
    @DecimalMax(value = "50000000.00", message = "하루 출금 한도액은 50,000,000원을 넘을 수 없습니다.")
    private BigDecimal dailyWithdrawalLimit;
}