package com.msa.banking.product.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanRunRequest {
    @NotBlank(message = "계좌 번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호를 xxx-xxxx-xxxxxxx 형식에 맞게 입력해주세요.")
    private String accountNumber;

    @NotNull(message = "계좌id는 필수 입력 사항입니다.")
    private UUID accountId;
}
