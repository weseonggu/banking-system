package com.msa.banking.personal.application.dto.personalHistory;

import com.msa.banking.personal.domain.enums.PersonalHistoryStatus;
import com.msa.banking.personal.domain.enums.PersonalHistoryType;
import com.msa.banking.personal.domain.model.PersonalHistory;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class PersonalHistoryResponseDto implements Serializable {

    private Long historyId;
    private String categoryName;
    private PersonalHistoryType type;
    private BigDecimal amount;
    private String description;
    private PersonalHistoryStatus status;

    public static PersonalHistoryResponseDto toDTO(PersonalHistory personalHistory){
        return PersonalHistoryResponseDto.builder()
                .historyId(personalHistory.getId())
                .categoryName(personalHistory.getCategory() != null ? personalHistory.getCategory().getName() : "Uncategorized")
                .type(personalHistory.getType())
                .amount(personalHistory.getAmount())
                .description(personalHistory.getDescription())
                .status(personalHistory.getStatus())
                .build();
    }
}
