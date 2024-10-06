package com.msa.banking.product.presentation.request;

import com.msa.banking.product.domain.ProductType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateCheckingProduct{

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Product type cannot be null")
    private ProductType type;

    @NotNull(message = "Valid from date cannot be null")
    @FutureOrPresent(message = "Valid from date must be in the present or future")
    private LocalDateTime valid_from;

    @Future(message = "Valid to date must be in the future")
    private LocalDateTime valid_to;

    @NotBlank(message = "Checking detail cannot be blank")
    private String chcking_detail;

    @NotBlank(message = "Terms and conditions cannot be blank")
    private String terms_and_conditions;

    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @Digits(integer = 1, fraction = 4, message = "Interest rate should be a decimal value with up to 3 integer digits and 2 fractional digits")
    private BigDecimal interest_rate;

    @Min(value = 0, message = "Fees must be zero or greater")
    private int fees;

    @NotNull(message = "File ID cannot be null")
    private Long fileId;

    @NotBlank(message = "File name cannot be blank")
    private String fileName;
}
