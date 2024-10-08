package com.msa.banking.personal.infrastructure.client;

import com.msa.banking.personal.application.service.UserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "AuthService")
public interface UserClient extends UserService {
    @Override
    @GetMapping("/api/users/customer/{customer_id}")
    ResponseEntity<?> findCustomerById(@PathVariable("customer_id") UUID customerId,
                                       @RequestHeader("X-User-Id") UUID userId,
                                       @RequestHeader("X-Role") String userRole
    );
}
