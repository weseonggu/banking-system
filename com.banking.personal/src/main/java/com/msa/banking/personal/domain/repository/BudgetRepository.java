package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.Budget;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {
    Budget save(Budget budget);

    Optional<Budget> findById(UUID budgetId);
}
