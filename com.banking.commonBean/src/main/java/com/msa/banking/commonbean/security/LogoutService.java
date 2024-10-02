package com.msa.banking.commonbean.security;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // JWT 토큰의 만료 시간 추출 (JWT 파싱 로직에 따라 구현)
            long expiration = getExpirationTimeFromJwt(token);

            // Redis에 토큰을 블랙리스트로 추가, 만료 시간 동안 저장
            redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);

            log.info("JWT 토큰이 블랙리스트에 추가되었습니다: " + token);
        } else {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED);
        }

    }

    private long getExpirationTimeFromJwt(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
