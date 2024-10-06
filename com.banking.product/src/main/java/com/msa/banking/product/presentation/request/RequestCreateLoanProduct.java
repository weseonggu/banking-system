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
public class RequestCreateLoanProduct {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Product type cannot be null")
    private ProductType type;

    @NotNull(message = "Valid from date cannot be null")
    @FutureOrPresent(message = "Valid from date must be in the present or future")
    private LocalDateTime valid_from;

    @Future(message = "Valid to date must be in the future")
    private LocalDateTime valid_to;

    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @Digits(integer = 1, fraction = 4, message = "Interest rate should be a decimal value with up to 3 integer digits and 2 fractional digits")
    private BigDecimal interestRate;

    @Min(value = 0, message = "Amount must be zero or greater")
    private Long minAmount;

    @Min(value = 0, message = "Amount must be zero or greater")
    private Long maxAmount;

    @Min(value = 0, message = "Loan term must be zero or greater")
    private int loanTerm;

    @NotBlank(message = "Preferential interestRates cannot be blank")
    private String preferentialInterestRates;

    @NotBlank(message = "Loan detail cannot be blank")
    private String loanDetail;

    @NotBlank(message = "Terms and conditions cannot be blank")
    private String terms_and_conditions;

    @NotNull(message = "File ID cannot be null")
    private Long fileId;

    @NotBlank(message = "File name cannot be blank")
    private String fileName;
}
