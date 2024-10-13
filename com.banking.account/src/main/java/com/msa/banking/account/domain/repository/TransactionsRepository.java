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

    // 계좌 입금 거래 금액 합산
    @Query("SELECT SUM(t.depositAmount) FROM AccountTransactions t WHERE t.account.accountId = :accountId")
    BigDecimal getTotalDepositBalance(@Param("accountId") UUID accountId);

    // 계좌 출금 거래 금액 합산
    @Query("SELECT SUM(t.withdrawalAmount) FROM AccountTransactions t WHERE t.account.accountId = :accountId")
    BigDecimal getTotalWithdrawalBalance(@Param("accountId") UUID accountId);

    @Query("select SUM(t.depositAmount) from AccountTransactions t where t.account.accountId in :accountIds and t.type = :type and t.createdAt between :startDateTime and :endDateTime and t.isDelete = false")
    BigDecimal findTotalDepositAmount(@Param("accountIds") List<UUID> accountIds,
                               @Param("type") TransactionType type,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("endDateTime") LocalDateTime endDateTime);

    @Query("select SUM(t.withdrawalAmount) from AccountTransactions t where t.account.accountId in :accountIds and t.type = :type and t.createdAt between :startDateTime and :endDateTime and t.isDelete = false")
    BigDecimal findTotalWithdrawalAmount(@Param("accountIds") List<UUID> accountIds,
                               @Param("type") TransactionType type,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("endDateTime") LocalDateTime endDateTime);


}
