package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.Budget;
import com.msa.banking.personal.domain.repository.BudgetRepository;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BudgetRepositoryImpl extends JpaRepository<Budget, UUID>, BudgetRepository {

    @Override
    Page<Budget> findAllByIsDeleteFalse(Pageable pageable);

    @Override
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId")
    Page<Budget> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);


    @Override
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.startDate <= :transactionDate AND b.endDate >= :transactionDate AND b.isDelete = false")
    List<Budget> findAllByUserIdAndPeriod(@Param("userId") UUID userId,
                                          @Param("transactionDate") LocalDateTime transactionDate);
}
