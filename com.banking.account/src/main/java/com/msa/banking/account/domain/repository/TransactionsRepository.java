package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.account.domain.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface TransactionsRepository extends JpaRepository<AccountTransactions, Long>, TransactionsRepositoryCustom {

    // 계좌 거래 금액 합산
    @Query("SELECT SUM(t.amount) FROM AccountTransactions t WHERE t.account.accountId = :accountId")
    BigDecimal getTotalBalance(@Param("accountId") UUID accountId);

    @Query("select SUM(t.amount) from AccountTransactions t where t.account.accountId in :accountIds and t.type = :type and t.createdAt between :startDateTime and :endDateTime and t.isDelete = false")
    BigDecimal findTotalAmount(@Param("accountIds") List<UUID> accountIds,
                               @Param("type") TransactionType type,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("endDateTime") LocalDateTime endDateTime);
}
