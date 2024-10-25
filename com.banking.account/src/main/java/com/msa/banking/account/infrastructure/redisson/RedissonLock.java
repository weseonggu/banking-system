package com.msa.banking.account.infrastructure.redisson;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String[] value(); // Lock의 이름
    long waitTime() default 1000L; // Lock 획득 대기 시간 (ms)
    long leaseTime() default 3000L; // 락 점유 시간 (ms), 입금 처리 시간을 고려하여 충분히 늘림
}