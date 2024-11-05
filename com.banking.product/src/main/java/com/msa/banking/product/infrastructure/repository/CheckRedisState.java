package com.msa.banking.product.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class CheckRedisState {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public boolean isRedisAvailable() {
//        try {
//            // Redis에 ping 요청을 보내고 응답이 올 경우 연결이 가능하다고 판단
//            redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG");
//            return true;
//        } catch (Exception e) {
//            // 연결이 불가능한 경우
//            return false;
//        }
//    }
//}

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckRedisState {

    private final RedisTemplate<String, Object> redisTemplate;

    private boolean redisAvailable;


    @Scheduled(fixedRate = 5000) // 5초마다 Redis 상태 체크
    public void checkRedisAvailability() {
        try {
            // Redis에 ping 요청을 보내고 응답이 올 경우 연결이 가능하다고 판단
            redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG");
            this.redisAvailable = true; // Redis가 사용 가능한 경우
        } catch (Exception e) {
            // 연결이 불가능한 경우
            this.redisAvailable = false; // Redis가 사용 불가능한 경우
            log.error("Check Redis: "+ redisAvailable);
        }
    }

    public boolean isRedisAvailable() {
        return redisAvailable;
    }
}