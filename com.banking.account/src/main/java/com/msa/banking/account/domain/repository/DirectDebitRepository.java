package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.DirectDebit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface DirectDebitRepository extends JpaRepository<DirectDebit, UUID>, DirectDebitRepositoryCustom {

}
