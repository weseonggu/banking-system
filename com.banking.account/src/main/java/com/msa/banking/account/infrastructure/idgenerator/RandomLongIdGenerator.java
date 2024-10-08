package com.msa.banking.account.infrastructure.idgenerator;

import java.security.SecureRandom;

public class RandomLongIdGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static Long generateId() {
        return Math.abs(secureRandom.nextLong());  // 예측 불가능한 Long 타입 난수 생성
    }
}