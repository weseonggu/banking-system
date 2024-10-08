package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.Budget;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {
    Budget save(Budget budget);

    Optional<Budget> findById(UUID budgetId);

    Page<Budget> findAllByIsDeleteFalse(Pageable pageable);

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.startDate <= :transactionDate AND b.endDate >= :transactionDate AND b.isDelete = false")
    List<Budget> findAllByUserIdAndPeriod(@Param("userId") UUID userId,
                                          @Param("transactionDate") LocalDateTime transactionDate);
}
