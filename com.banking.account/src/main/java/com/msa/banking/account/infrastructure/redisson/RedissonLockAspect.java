package com.msa.banking.account.infrastructure.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(redissonLock)")
    public Object handleRedissonLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        String[] lockKeys = redissonLock.value();
        RLock[] locks = new RLock[lockKeys.length];

        try {
            // 모든 락에 대해 획득 시도
            for (int i = 0; i < lockKeys.length; i++) {
                String lockKey = CustomSpringELParser.getDynamicValue(
                        ((MethodSignature) joinPoint.getSignature()).getParameterNames(),
                        joinPoint.getArgs(),
                        lockKeys[i]);
                locks[i] = redissonClient.getLock(lockKey);
                boolean lockAcquired = locks[i].tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.MILLISECONDS);
                if (!lockAcquired) {
                    throw new RuntimeException("Unable to acquire lock for: " + lockKey);
                }
                log.info("Lock acquired for {}", lockKey);
            }

            // 모든 락을 성공적으로 획득한 경우 비즈니스 로직 실행
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            // 모든 락 해제
            for (RLock lock : locks) {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    log.info("Releasing lock");
                    lock.unlock();
                }
            }
        }
    }
}
