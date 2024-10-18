package com.msa.banking.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackIdRequestDto {

    @NotBlank(message = "슬랙 ID 는 필수 입니다.")
    @Schema(description = "슬랙 ID", example = "U07MFBXHTG9", type = "string")
    private String slackId;
}
