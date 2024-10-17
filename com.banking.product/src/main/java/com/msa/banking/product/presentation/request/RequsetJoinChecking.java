package com.msa.banking.product.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("product_type")
    private ProductType type;

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
