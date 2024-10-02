package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.domain.repository.EmployeeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, EmployeeRepositoryCustom {
    
    Optional<Employee> findByUsername(String username);
}
