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

//        // 현재 스레드에서 SecurityContext를 가져옴
//        SecurityContext context = SecurityContextHolder.getContext();
//
//        // SecurityContext가 비어 있거나, 인증 정보가 없는 경우 새로 생성
//        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
//
//            // 사용자 정보를 기반으로 인증 토큰 생성
//            Authentication authentication = null;
//            if (UserRole.valueOf(role) == UserRole.MASTER || UserRole.valueOf(role) == UserRole.MANAGER) {
//                authentication = createEmployeeAuthentication(userId);
//
//            } else if (UserRole.valueOf(role) == UserRole.CUSTOMER) {
//                authentication = createCustomerAuthentication(userId);
//
//            } else {
//                log.error("userId, userName or role is null");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Missing or invalid authentication details");
//                return; // 유효하지 않은 경우 종료
//            }
//
//            // SecurityContext에 인증 정보 설정
//            context = SecurityContextHolder.createEmptyContext(); // 새 컨텍스트 생성
//            context.setAuthentication(authentication);
//            SecurityContextHolder.setContext(context);
//        } else {
//            log.info("이미 인증된 컨텍스트가 존재합니다.");
//        }
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
