package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.DirectDebit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


public interface DirectDebitRepository extends JpaRepository<DirectDebit, UUID>, DirectDebitRepositoryCustom {
}
