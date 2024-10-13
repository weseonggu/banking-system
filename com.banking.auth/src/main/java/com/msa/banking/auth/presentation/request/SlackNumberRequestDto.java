package com.msa.banking.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackNumberRequestDto {

    @NotBlank(message = "슬랙 ID는 필수 입니다.")
    private String slackId;

    @NotBlank(message = "슬랙 인증번호는 필수 입니다.")
    private String slackNumber;
}
