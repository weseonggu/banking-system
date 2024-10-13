package com.msa.banking.common.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackIdRequestDto {

    @NotBlank(message = "슬랙 ID 는 필수 입니다.")
    private String slackId;
}
