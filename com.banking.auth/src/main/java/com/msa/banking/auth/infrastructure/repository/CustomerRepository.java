package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.repository.CustomerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, CustomerRepositoryCustom {

    Optional<Customer> findByUsername(String username);
}
