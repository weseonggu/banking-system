package com.msa.banking.product.presentation.request;

import com.msa.banking.product.lib.ProductType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequsetJoinChecking{

    @NotNull(message = "필수 입력 사항입니다.")
    private UUID userId;


    @NotNull(message = "필수 입력 사항입니다.")
    private ProductType type;

    @NotNull(message = "필수 입력 사항입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @Digits(integer = 1, fraction = 4, message = "Interest rate should be a decimal value with up to 3 integer digits and 2 fractional digits")
    private BigDecimal interestRate;

    @NotNull(message = "필수 입력 사항입니다.")
    private Boolean feeWaiver;

    @NotNull(message = "필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "6자리 숫자여야 합니다.")
    private String accountPin;// 비번

    @NotNull(message = "필수 입력 사항입니다.")
    @NotBlank(message = "필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "필수 입력 사항입니다.")
    private UUID productId;
}
