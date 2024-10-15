package com.msa.banking.product.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
@Deprecated
@FeignClient(name = "AuthService", contextId = "authClient2")
public interface AuthClient2 {
    @GetMapping(value = "/api/users/customer/check")
    Boolean findByUserIdAndName(
            @RequestHeader("X-User-Id") UUID id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            @RequestParam("userId") UUID userId,
            @RequestParam("name") String name);
}

