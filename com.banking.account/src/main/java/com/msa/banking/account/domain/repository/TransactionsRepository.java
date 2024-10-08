package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.AccountTransactions;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;


public interface TransactionsRepository extends JpaRepository<AccountTransactions, Long>, TransactionsRepositoryCustom {

    // 계좌 거래 금액 합산
    @Query("SELECT SUM(t.amount) FROM AccountTransactions t WHERE t.account.accountId = :accountId")
    BigDecimal getTotalBalance(@Param("accountId") UUID accountId);
}
