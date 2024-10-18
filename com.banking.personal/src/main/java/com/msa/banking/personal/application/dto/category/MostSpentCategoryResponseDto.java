package com.msa.banking.personal.application.dto.category;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MostSpentCategoryResponseDto {

    String categoryName;
    BigDecimal totalSpent;
}
