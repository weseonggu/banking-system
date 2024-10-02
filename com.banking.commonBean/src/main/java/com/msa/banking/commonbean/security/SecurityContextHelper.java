package com.msa.banking.commonbean.security;

import com.msa.banking.common.base.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextHelper {

    private final EmployeeDetailsServiceImpl employeeDetailsService;
    private final CustomerDetailsServiceImpl customerDetailsService;

    /**
     * 인증 정보 설정
     *
     * @param userId
     * @param role
     * @param response
     */
    public void setAuthentication(String userId, String role, HttpServletResponse response) throws IOException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 사용자 정보를 기반으로 인증 토큰 생성
        Authentication authentication = null;
        if (UserRole.valueOf(role) == UserRole.MASTER || UserRole.valueOf(role) == UserRole.MANAGER) {
            authentication = createEmployeeAuthentication(userId);

        } else if (UserRole.valueOf(role) == UserRole.CUSTOMER) {
            authentication = createCustomerAuthentication(userId);

        } else {
            log.error("userId, userName or role is null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid authentication details");
        }

        // SecurityContext에 인증 정보 설정
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    /**
     * 인증 객체 생성, 고객
     * @param userId
     * @return
     */
    private Authentication createCustomerAuthentication(String userId) {
        UserDetails userDetails = customerDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 인증 객체 생성, 직원
     * @param userId
     * @return
     */
    private Authentication createEmployeeAuthentication(String userId) {
        UserDetails userDetails = employeeDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
