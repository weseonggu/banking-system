package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.AccountTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface TransactionsRepository extends JpaRepository<AccountTransactions, Long>, TransactionsRepositoryCustom {



}
