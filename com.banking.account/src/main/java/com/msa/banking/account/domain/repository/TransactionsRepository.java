package com.msa.banking.account.domain.repository;

import com.msa.banking.account.application.dto.FirstBatchTransactionsDto;
import com.msa.banking.account.domain.model.AccountTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface TransactionsRepository extends JpaRepository<AccountTransactions, Long>, TransactionsRepositoryCustom {

    @Query("select coalesce(sum(at.depositAmount), 0) from AccountTransactions at " +
            "where at.account.accountId in :accountIds and at.createdAt between :startDateTime and :endDateTime and at.isDelete = false")
    BigDecimal findTotalDepositAmountAndAccountIds(@Param("accountIds") List<UUID> accountIds,
                                                          @Param("startDateTime")LocalDateTime startDateTime,
                                                          @Param("endDateTime")LocalDateTime endDateTime);

    @Query("select new com.msa.banking.account.application.dto.FirstBatchTransactionsDto(a.account.accountId, a.depositAmount, a.withdrawalAmount) " +
            "from AccountTransactions a  where a.account.accountId = :accountId")
    List<FirstBatchTransactionsDto> findByAccountId(@Param("accountId") UUID accountId);
}
