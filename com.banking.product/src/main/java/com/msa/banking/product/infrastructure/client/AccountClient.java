package com.msa.banking.product.infrastructure.client;

import com.msa.banking.common.account.dto.AccountRequestDto;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "AccountService")
public interface AccountClient {

    @PostMapping("/api/account/")
    ResponseEntity<UUID> addAccount(@RequestBody AccountRequestDto accountRequestDto);
}
