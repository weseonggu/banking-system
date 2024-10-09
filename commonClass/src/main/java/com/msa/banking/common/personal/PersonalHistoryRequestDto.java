package com.msa.banking.common.personal;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PersonalHistoryRequestDto {

    private UUID userId;
    private BigDecimal amount;
    private PersonalHistoryType type;
    private String description;
    private LocalDateTime transactionDate;
}
