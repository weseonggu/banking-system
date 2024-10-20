package com.msa.banking.product.infrastructure.client;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.LoanDepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "AccountService")
public interface AccountClient {

    @PostMapping("/api/accounts")
    ResponseEntity<UUID> addAccount(@RequestBody AccountRequestDto accountRequestDto);

    @PostMapping("/api/account-transactions/{account_id}/loan-deposit")
    public ResponseEntity<SingleTransactionResponseDto> updateAccount(
            @PathVariable("account_id") UUID accountId,
            @RequestBody LoanDepositTransactionRequestDto request);

    @DeleteMapping("/api/accounts/{account_id}")
    public Boolean deleteAccount(@PathVariable("account_id") UUID accountId);

    @DeleteMapping("/api/accounts/{account_id}/loan")
    public Boolean deleteLoanAccount( @PathVariable("account_id") UUID accountId,
                                      @RequestParam BigDecimal amount);
}
