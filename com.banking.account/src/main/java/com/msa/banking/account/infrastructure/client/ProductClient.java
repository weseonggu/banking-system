package com.msa.banking.account.infrastructure.client;

import com.msa.banking.account.application.service.ProductService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("ProductService")
public interface ProductClient extends ProductService {

    @Override
    @GetMapping("/product/find/{accountId}")
    ResponseEntity<?> findByAccountId(@PathVariable UUID accountId,
                                      @RequestHeader("X-User-Id") UUID userId,
                                      @RequestHeader("X-Role") String userRole);
}
