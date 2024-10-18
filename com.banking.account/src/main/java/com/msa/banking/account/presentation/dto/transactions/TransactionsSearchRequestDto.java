package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.common.account.type.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsSearchRequestDto {

    private TransactionType type;

    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{7}", message = "계좌 번호 형식을 맞게 입력해주세요.")
    private String accountNumber; // 내역을 살펴야 할 계좌 번호

    // 거래 발생 일자
    private LocalDate searchDate;
}
