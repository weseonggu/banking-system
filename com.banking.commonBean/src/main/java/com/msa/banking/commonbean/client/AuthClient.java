package com.msa.banking.commonbean.client;

import com.msa.banking.common.auth.response.AuthFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AuthService")
public interface AuthClient {

    @GetMapping("/api/users/employee/info")
    AuthFeignResponseDto findEmployeeId(@RequestParam("userId") String userId);

    @GetMapping("/api/users/customer/info")
    AuthFeignResponseDto findCustomerId(@RequestParam("userId") String userId);
}
