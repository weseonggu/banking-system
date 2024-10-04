package com.msa.banking.commonbean.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j(topic = "CustomPreAuthFilter")
@RequiredArgsConstructor
public class CustomPreAuthFilter extends OncePerRequestFilter {

    private final SecurityContextHelper securityContextHelper;
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
        String userName = request.getHeader("X-Username");
        String role = request.getHeader("X-Role");

        log.info("userId: {}, userName: {}, role: {}", userId, userName, role);

        if (userId != null && userName != null && role != null) {

            try {
                log.info("인증 설정 시도 중");
                securityContextHelper.setAuthentication(userId, role, response);
                log.info("인증 설정 성공");
            } catch (Exception e) {
                log.error("인증 설정 실패: {}", e.getMessage());
                return;
            }

            // Redis에서 블랙리스트 확인
            String authorization = request.getHeader("Authorization");

            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);

                if (redisTemplate.hasKey(token)) {
                    // 블랙리스트에 있으면 인증 실패 처리
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"해당 토큰은 로그아웃 처리되었습니다.\"}");
                    SecurityContextHolder.clearContext();
                    return;
                }
            }

        }else {
            log.error("userId, userName or role is null");

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
