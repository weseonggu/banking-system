package com.msa.banking.account.domain.model;

public enum AccountStatus {
    ACTIVE,  // 계좌 활성화
    DORMANT, // 계좌 휴면
    FROZEN,  // 계좌 동결
    CLOSED   // 계좌 해지
}
