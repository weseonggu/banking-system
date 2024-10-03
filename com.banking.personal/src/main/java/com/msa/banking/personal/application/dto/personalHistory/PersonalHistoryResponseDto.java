package com.msa.banking.personal.application.dto.personalHistory;

import com.msa.banking.personal.domain.enums.PersonalHistoryType;
import com.msa.banking.personal.domain.model.PersonalHistory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PersonalHistoryResponseDto {

    private Long historyId;
    private String categoryName;
    private PersonalHistoryType type;
    private BigDecimal amount;
    private String description;
    private boolean status;

    public static PersonalHistoryResponseDto toDTO(PersonalHistory personalHistory){
        return PersonalHistoryResponseDto.builder()
                .historyId(personalHistory.getId())
                .categoryName(personalHistory.getCategory().getName())
                .type(personalHistory.getType())
                .amount(personalHistory.getAmount())
                .description(personalHistory.getDescription())
                .status(personalHistory.isStatus())
                .build();
    }
}
