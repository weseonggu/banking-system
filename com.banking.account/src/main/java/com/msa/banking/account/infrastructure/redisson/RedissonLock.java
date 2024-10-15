package com.msa.banking.account.infrastructure.redisson;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String[] value(); // Lock의 이름
    long waitTime() default 5000L; // Lock 획득 대기 시간 (ms)
    long leaseTime() default 2000L; // 락 점유 시간 (ms)
}