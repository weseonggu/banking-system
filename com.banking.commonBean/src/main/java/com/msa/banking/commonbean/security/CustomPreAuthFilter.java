package com.msa.banking.commonbean.security;

import com.msa.banking.common.auth.dto.ForContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j(topic = "CustomPreAuthFilter")
@RequiredArgsConstructor
public class CustomPreAuthFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("path: {}", path);

        if (path.startsWith("/api/auth/signUp") || path.startsWith("/api/auth/signIn")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("CustomPreAuthFilter 필터링 시도 중");

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String role = request.getHeader("X-Role");

        log.info("userId: {}, userName: {}, role: {}", userId, username, role);

        if (userId != null && username != null && role != null) {

            try {
                log.info("인증 설정 시도 중");
                ForContext context = new ForContext(UUID.fromString(userId), username, role);

                UserDetailsImpl userDetails = new UserDetailsImpl(context);
                Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("인증 설정 성공");
            } catch (Exception e) {
                log.error("인증 설정 실패: {}", e.getMessage());
                return;
            }

            // Redis에서 블랙리스트 확인
            String authorization = request.getHeader("Authorization");

            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);

                try {
                    // Redis에서 해당 토큰이 블랙리스트에 있는지 확인
                    if (redisTemplate != null && redisTemplate.hasKey(token)) {
                        // 블랙리스트에 있으면 인증 실패 처리
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"해당 토큰은 로그아웃 처리되었습니다.\"}");
                        SecurityContextHolder.clearContext();
                        return;
                    }
                } catch (Exception e) {
                    // Redis에서 조회 중 예외 발생 시 처리
                    log.error("Redis 조회 중 오류 발생: {}", e.getMessage());
                    // Redis가 실패해도 인증 로직은 계속 처리 (필요에 따라 로직 수정 가능)

                }
            }

        }else {
            log.error("userId, userName or role is null");

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"필수 헤더 값이 없습니다. X-User-Id, X-Username, X-Role 헤더가 필요합니다.\"}");
            return;

        }

        filterChain.doFilter(request, response);
    }
}
