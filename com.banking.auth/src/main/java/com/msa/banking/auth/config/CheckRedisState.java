package com.msa.banking.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckRedisState {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isRedisAvailable() {
        try {
            // Redis에 ping 요청을 보내고 응답이 올 경우 연결이 가능하다고 판단
            return redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG");
        } catch (Exception e) {
            // 연결이 불가능한 경우
            return false;
        }
    }
}
