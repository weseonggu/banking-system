package com.msa.banking.commonbean.client;

import com.msa.banking.common.auth.response.AuthFeignResponseDto;
import com.msa.banking.common.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "AuthService")
public interface AuthClient {

    @GetMapping("/api/users/employee/info")
    AuthFeignResponseDto findEmployeeId(@RequestParam("userId") String userId);

    @GetMapping("/api/users/customer/info")
    AuthFeignResponseDto findCustomerId(@RequestParam("userId") String userId);

    @GetMapping("/api/users/customer/{customer_id}")
    SuccessResponse<AuthFeignResponseDto> findOneCustomer(@PathVariable("customer_id") UUID customerId);

    @GetMapping("/api/users/employee/{employee_id}")
    SuccessResponse<AuthFeignResponseDto> findOneEmployee(@PathVariable("employee_id") UUID employeeId);

    @GetMapping(value = "/api/users/customer/check")
    Boolean findByUserIdAndName(@RequestParam("userId") UUID userId, @RequestParam("name") String name);
}
