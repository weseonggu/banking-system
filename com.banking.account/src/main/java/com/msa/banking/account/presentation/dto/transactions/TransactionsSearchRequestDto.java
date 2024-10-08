package com.msa.banking.account.presentation.dto.transactions;

import com.msa.banking.account.domain.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsSearchRequestDto {

    private TransactionType type;
    private String accountNumber; // 내역을 살펴야 할 계좌 번호

    // 거래 발생 일자
    private LocalDate searchDate;
}
