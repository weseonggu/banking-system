package com.msa.banking.account.domain.model;

public enum TransactionType {
    DEPOSIT,           // 입금
    WITHDRAWAL,        // 출금
    TRANSFER,          // 이체
    PAYMENT,           // 결제
    SAVINGS_DEPOSIT,   // 저축
    LOAN_REPAYMENT     // 대출 상환
}
