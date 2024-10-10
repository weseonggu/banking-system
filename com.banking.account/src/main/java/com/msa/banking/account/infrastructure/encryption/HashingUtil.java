package com.msa.banking.account.infrastructure.encryption;

import org.apache.commons.codec.digest.DigestUtils;

public class HashingUtil {

    // SHA-256 해시 함수
    public static String hashAccountNumber(String accountNumber) {
        // accountNumber가 null일 경우 대비
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        // SHA-256으로 해시 생성
        return DigestUtils.sha256Hex(accountNumber);
    }
}