package com.msa.banking.product.infrastructure.client.fallback;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.common.account.dto.LoanDepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.presentation.exception.custom.TryAgainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;


@Slf4j(topic = "AccountClientAtProduct")
@Deprecated
public class AccountClientFallback implements AccountClient {

    @Override
    public ResponseEntity<UUID> addAccount(AccountRequestDto accountRequestDto) {
        log.error("AccountClient 문제 발생");
        throw new TryAgainException("");
    }

    @Override
    public ResponseEntity<SingleTransactionResponseDto> updateAccount(UUID accountId, LoanDepositTransactionRequestDto request) {
        log.error("AccountClient 문제 발생");
        throw new TryAgainException("잠시 후 다시 시도 해주세요");
    }

    @Override
    public Boolean deleteAccount(UUID accountId) {
        return null;
    }

    @Override
    public Boolean deleteLoanAccount(UUID accountId, BigDecimal amount) {
        return null;
    }

}
