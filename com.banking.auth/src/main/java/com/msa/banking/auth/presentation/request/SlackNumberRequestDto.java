package com.msa.banking.auth.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackNumberRequestDto {

    @NotBlank(message = "슬랙 ID는 필수 입니다.")
    @Schema(description = "슬랙 ID", example = "U07MFBXHTG9", type = "string")
    private String slackId;

    @NotBlank(message = "슬랙 인증번호는 필수 입니다.")
    @Schema(description = "슬랙 인증번호", example = "123456", type = "string")
    private String slackNumber;
}
