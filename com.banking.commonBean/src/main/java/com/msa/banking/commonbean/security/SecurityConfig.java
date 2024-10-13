package com.msa.banking.commonbean.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "common.bean.security.class", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomPreAuthFilter customPreAuthFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(name = "common.bean.security.auth", havingValue = "true", matchIfMissing = false)
    SecurityFilterChain AuthSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(customPreAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Auth Server 를 제외한 다른 모듈을요청 접근 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/notifications/slack-code/**").permitAll()
                .requestMatchers("/product/**").permitAll()// 임시
                .anyRequest().authenticated()
        );

        // 권한이 없는 사용자가 접근할 때의 처리
        http.exceptionHandling(handler -> handler
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"사용자의 권한으로는 접근이 불가합니다.\"}");
                }));

        return http.build();
    }

}
