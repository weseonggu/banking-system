package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, AccountRepositoryCustom {

    Optional<Account> findByAccountNumber(String accountNumber);
}
