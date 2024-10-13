package com.msa.banking.product.infrastructure.client;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionRequestDto;
import com.msa.banking.common.account.dto.TransactionResponseDto;
import com.msa.banking.product.infrastructure.client.fallback.AccountClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "AccountService", fallback =  AccountClientFallback.class)
public interface AccountClient {

    @PostMapping("/api/accounts")
    ResponseEntity<UUID> addAccount(@RequestBody AccountRequestDto accountRequestDto);

    @PostMapping("/api/account-transactions/{account_id}/deposit")
    public ResponseEntity<TransactionResponseDto> updateAccount(
            @PathVariable("account_id") UUID accountId,
            @RequestBody SingleTransactionRequestDto request);
}
