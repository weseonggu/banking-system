package com.msa.banking.auth.infrastructure.repository;

import com.msa.banking.auth.domain.model.Employee;
import com.msa.banking.auth.domain.repository.EmployeeRepositoryCustom;
import com.msa.banking.common.base.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, EmployeeRepositoryCustom {
    
    Optional<Employee> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select e.slackId from Employee e where e.role = :role")
    List<String> findByRole(@Param("role") UserRole role);
}
