package com.msa.banking.product.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa.banking.commonbean.annotation.ValidNumberRatio;
import com.msa.banking.product.lib.ProductType;
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
    @JsonProperty("product_name")
    @ValidNumberRatio(maxRatio = 0.3, message = "The number in the title can be 30 percent.")
    private String name;

    @NotNull(message = "Product type cannot be null")
    @JsonProperty("product_type")
    private ProductType type;

    @NotNull(message = "Valid from date cannot be null")
    @FutureOrPresent(message = "Valid from date must be in the present or future")
    @JsonProperty("Subscription_start_date")
    private LocalDateTime valid_from;

    @Future(message = "Valid to date must be in the future")
    @JsonProperty("Subscription_end_date")
    private LocalDateTime valid_to;

    @NotBlank(message = "Checking detail cannot be blank")
    @JsonProperty("product_detail_content")
    private String chcking_detail;

    @NotBlank(message = "Terms and conditions cannot be blank")
    @JsonProperty("product_terms_and_conditions")
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
    @JsonProperty("upload_file_name")
    private String fileName;
}
