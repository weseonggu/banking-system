package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.Account;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, AccountRepositoryCustom {

    // 계좌번호로 계좌 가져오기
    Optional<Account> findByAccountNumber(String accountNumber);

    // 계좌 거래 시 계좌번호 가져오기
    @Query("SELECT a.accountNumber FROM Account a WHERE a.accountId = :accountId")
    String getAccountNumber(@Param("accountId") UUID accountId);

    // 계좌 아이디로 소유자 이름 가져오기
    @Query("SELECT a.accountHolder FROM Account a WHERE a.accountId = :accountId")
    String getAccountHolder(@Param("accountId") UUID accountId);
}
