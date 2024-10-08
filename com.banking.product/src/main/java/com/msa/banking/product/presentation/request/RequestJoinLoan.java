package com.msa.banking.product.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestJoinLoan {

    @NotNull(message = "필수 입력 사항입니다.")
    @NotBlank(message = "필수 입력 사항입니다.")
    private UUID userId;

    @NotNull(message = "필수 입력 사항입니다.")
    @NotBlank(message = "필수 입력 사항입니다.")
    private UUID productId;

    @NotNull(message = "필수 입력 사항입니다.")
    @NotBlank(message = "필수 입력 사항입니다.")
    private double loanAmount;

    @NotNull(message = "필수 입력 사항입니다.")
    @NotBlank(message = "필수 입력 사항입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @Digits(integer = 2, fraction = 4, message = "Interest rate should be a decimal value with up to 3 integer digits and 2 fractional digits")
    private BigDecimal interestRate;
}
