package com.msa.banking.product.infrastructure.client;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.DepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.commonbean.security.UserDetailsImpl;
import com.msa.banking.product.infrastructure.client.fallback.AccountClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "AccountService")
public interface AccountClient {

    @PostMapping("/api/accounts")
    ResponseEntity<UUID> addAccount(@RequestBody AccountRequestDto accountRequestDto);

    @PostMapping("/api/account-transactions/{account_id}/loan-deposit")
    public ResponseEntity<SingleTransactionResponseDto> updateAccount(
            @PathVariable("account_id") UUID accountId,
            @RequestBody DepositTransactionRequestDto request);

    @DeleteMapping("/api/accounts/{account_id}")
    public Boolean deleteAccount(@PathVariable("account_id") UUID accountId);

    @DeleteMapping("/api/accounts/{account_id}/loan")
    public Boolean deleteLoanAccount(@PathVariable("account_id") UUID accountId, Long amount);
}
