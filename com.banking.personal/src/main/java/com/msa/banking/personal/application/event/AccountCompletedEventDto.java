package com.msa.banking.personal.application.event;

import com.msa.banking.personal.domain.enums.PersonalHistoryType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountCompletedEventDto {

    private UUID userId;
    private BigDecimal amount;
    private PersonalHistoryType type;
    private String description;
    private LocalDateTime transactionDate;
}
