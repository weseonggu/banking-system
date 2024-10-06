package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.model.PersonalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {
    Budget save(Budget budget);

    Optional<Budget> findById(UUID budgetId);

    Page<Budget> findAllByIsDeleteFalse(Pageable pageable);
}
