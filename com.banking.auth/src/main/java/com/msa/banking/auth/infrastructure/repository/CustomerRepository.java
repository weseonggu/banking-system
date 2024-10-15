package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.repository.CustomerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, CustomerRepositoryCustom {

    Optional<Customer> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdAndName(UUID userId, String name);

    boolean existsByUsernameAndIdNot(String username, UUID customerId);

    boolean existsByEmailAndIdNot(String email, UUID customerId);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID customerId);
}
