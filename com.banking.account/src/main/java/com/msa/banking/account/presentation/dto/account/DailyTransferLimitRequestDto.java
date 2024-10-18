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
public class DailyTransferLimitRequestDto {

    @NotBlank(message = "하루 이체 한도액은 필수 입력 사항입니다.")
    @DecimalMin(value = "100000.00", message = "하루 이체 한도액은 100,000원보다 커야 합니다.")
    @DecimalMax(value = "500000000.00", message = "하루 이체 한도액은 500,000,000원을 넘을 수 없습니다.")
    private BigDecimal dailyTransferLimit;
}