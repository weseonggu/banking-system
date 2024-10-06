package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.repository.BudgetRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BudgetRepositoryImpl extends JpaRepository<Budget, UUID>, BudgetRepository {
}
