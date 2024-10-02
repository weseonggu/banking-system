package com.msa.banking.commonbean.openfeign;

import com.msa.banking.commonbean.security.UserDetailsImpl;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("FeignClientInterceptor: 요청 전 Security Context 내용 해더에 넣기");

        // 시큐리티 컨텍스트가 null이 아닐 때만 실행
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            // 시큐리티 컨텍스트에서 인증 정보 가져오기
            Authentication authentication = context.getAuthentication();

            // 인증된 사용자가 존재하고 인증이 된 상태일 경우
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                // 사용자가 UserDetailsImpl일 경우
                if (principal instanceof UserDetailsImpl userDetailsImpl) {
                    Map<String, String> requestHeaders = new HashMap<>();
                    requestHeaders.put("X-User-Id", userDetailsImpl.getUserId().toString());
                    requestHeaders.put("X-Username", userDetailsImpl.getUsername());
                    requestHeaders.put("X-Role", userDetailsImpl.getRole());

                    // 헤더에 추가
                    requestHeaders.forEach(requestTemplate::header);
                }
            }
        } else {
            log.warn("Security Context가 null입니다.");
        }



        }
}
