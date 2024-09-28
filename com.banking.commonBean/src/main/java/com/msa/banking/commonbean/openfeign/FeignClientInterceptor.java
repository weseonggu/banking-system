package com.msa.banking.commonbean.openfeign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // TODO : Security Context의 내용 해더에 넣기
        log.info("FeignClientInterceptor: 요청 전 Security Context 내용 해더에 넣기");
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-User-id", "1");
        requestHeaders.put("X-User-Username", "TestUser");
        requestHeaders.put("X-User-Role", "User");

        requestHeaders.forEach(requestTemplate::header);


    }
}
