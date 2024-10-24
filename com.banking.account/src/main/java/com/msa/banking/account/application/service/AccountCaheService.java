package com.msa.banking.account.application.service;

import com.msa.banking.account.application.mapper.TransactionsMapper;
import com.msa.banking.account.config.RedisCacheKey;
import com.msa.banking.account.domain.model.Account;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.repository.AccountRepository;
import com.msa.banking.account.domain.repository.TransactionsRepository;
import com.msa.banking.account.infrastructure.redisson.RedissonLock;
import com.msa.banking.account.presentation.dto.transactions.DepositTransactionRequestDto;
import com.msa.banking.common.account.dto.SingleTransactionResponseDto;
import com.msa.banking.common.account.type.AccountStatus;
import com.msa.banking.common.account.type.TransactionType;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.annotation.LogDataChange;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountCaheService {

    private final TransactionalService transactionalService;
    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final TransactionsMapper transactionsMapper;
    private final ProductService productService;
    private final AccountService accountService;
    @LogDataChange
    @Cacheable(cacheNames = RedisCacheKey.AccountCache, key = "#request.accountNumber")
    public Account findAccountAndCaching(DepositTransactionRequestDto request) {


        // 입금하려는 계좌 찾기
        return accountRepository.findByAccountNumber(request.getAccountNumber())
                .filter(a -> !a.getIsDelete() && a.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.ACCOUNT_NOT_FOUND));

    }
}
