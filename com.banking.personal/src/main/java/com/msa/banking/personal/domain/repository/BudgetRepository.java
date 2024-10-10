package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {
    Budget save(Budget budget);

    Optional<Budget> findById(UUID budgetId);

    Page<Budget> findAllByIsDeleteFalse(Pageable pageable);

    List<Budget> findAllByUserIdAndPeriod(UUID userId, LocalDateTime transactionDate);
}
