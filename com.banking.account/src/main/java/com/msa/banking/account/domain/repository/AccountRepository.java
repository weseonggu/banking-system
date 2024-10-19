package com.msa.banking.account.domain.repository;

import com.msa.banking.account.application.dto.FirstBatchAccountResponseDto;
import com.msa.banking.account.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, AccountRepositoryCustom {

    // 계좌번호로 계좌 가져오기
    Optional<Account> findByAccountNumber(String accountNumber);

    // 주어진 accountIds 목록으로 Account 조회
    @Query("select a.accountNumber from Account a where a.accountId in :accountIds and a.createdAt between :startDateTime and :endDateTime and a.isDelete = false")
    List<String> findByAccountIdInAndCreatedAtBetween(@Param("accountIds") List<UUID> accountIds,
                                                      @Param("startDateTime") LocalDateTime startDateTime,
                                                      @Param("endDateTime") LocalDateTime endDateTime);

    // 모든 계좌 아이디와 계좌 잔액 가져오기
    @Query("select new com.msa.banking.account.application.dto.FirstBatchAccountResponseDto(a.accountId, a.balance) from Account a")
    List<FirstBatchAccountResponseDto> findAllAccountIdAndBalance();
}
