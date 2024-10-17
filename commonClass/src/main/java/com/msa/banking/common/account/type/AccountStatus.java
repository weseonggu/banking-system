package com.msa.banking.common.account.type;

public enum AccountStatus {
    ACTIVE,  // 계좌 활성화
    DORMANT, // 계좌 휴면
    FROZEN,  // 계좌 동결,  입출금, 이체, 타인 송금 불가능
    CLOSED,  // 계좌 해지
    LOCKED     // 계좌 잠금, 입출금, 이체는 불가능. 타인 송금은 받을 수 있다.
}
