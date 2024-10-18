package com.msa.banking.personal.application.dto.personalHistory;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PersonalHistoryUpdateDto {

    @NotBlank(message = "카테고리 이름을 설정해주세요.")
    private String categoryName;
}
