package com.msa.banking.product.infrastructure.client.fallback;

import com.msa.banking.common.account.dto.AccountRequestDto;
import com.msa.banking.product.infrastructure.client.AccountClient;
import com.msa.banking.product.presentation.exception.custom.TryAgainException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j(topic = "AccountClientAtProduct")
public class AccountClientFallback implements AccountClient {

    @Override
    public ResponseEntity<UUID> addAccount(AccountRequestDto accountRequestDto) {
        log.error("AccountClient 문제 발생");
        throw new TryAgainException("잠시 후 다시 시도 해주세요");
    }
}
