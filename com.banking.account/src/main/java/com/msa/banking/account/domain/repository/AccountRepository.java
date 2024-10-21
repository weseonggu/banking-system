package com.msa.banking.account.domain.repository;

import com.msa.banking.account.application.dto.FirstBatchAccountResponseDto;
import com.msa.banking.account.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, AccountRepositoryCustom {

    // 계좌번호로 계좌 가져오기
    Optional<Account> findByAccountNumber(String accountNumber);

    // 모든 계좌 아이디와 계좌 잔액 가져오기
    @Query("select new com.msa.banking.account.application.dto.FirstBatchAccountResponseDto(a.accountId, a.balance) from Account a")
    List<FirstBatchAccountResponseDto> findAllAccountIdAndBalance();
}
