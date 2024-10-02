package com.msa.banking.product.presentation.request;

import com.msa.banking.product.domain.ProductType;

import java.time.LocalDateTime;

public record RequestCreateCheckingProduct(
        String name,
        ProductType type,
        LocalDateTime valid_from,
        LocalDateTime valid_to,
        String chcking_detail,
        String terms_and_conditions,
        String interest_rate,
        int fees
) {
}
