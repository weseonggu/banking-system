package com.msa.banking.product.aspect;

import io.lettuce.core.RedisCommandTimeoutException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheableExceptionAspect {

    @Around("@annotation(cacheable)")
    public Object handleCacheExceptions(ProceedingJoinPoint pjp, Cacheable cacheable) throws Throwable {
        try {
            // @Cacheable 메서드 실행 시도
            return pjp.proceed();
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            // Redis 예외가 발생하면 로그 출력 후 캐시를 무시하고 DB에서 조회
            System.err.println("Redis access failed, bypassing cache and querying DB: " + e.getMessage());

            // 캐시 무시하고 메서드 실행 (DB 조회)
            return pjp.proceed();
        }
    }
}
