package com.msa.banking.personal.application.dto.personalHistory;

import com.msa.banking.personal.domain.enums.PersonalHistoryType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PersonalHistoryRequestDto {

    private BigDecimal amount;
    private PersonalHistoryType type;
    private String description;
    private LocalDateTime transactionDate;
}
