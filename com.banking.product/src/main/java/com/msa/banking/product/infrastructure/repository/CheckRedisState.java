package com.msa.banking.product.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckRedisState {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isRedisAvailable() {
        try {
            // Redis에 ping 요청을 보내고 응답이 올 경우 연결이 가능하다고 판단
            redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG");
            return true;
        } catch (Exception e) {
            // 연결이 불가능한 경우
            return false;
        }
    }
}
