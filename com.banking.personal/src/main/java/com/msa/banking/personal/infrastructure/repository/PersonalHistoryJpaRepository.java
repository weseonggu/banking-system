package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PersonalHistoryJpaRepository extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepositoryCustom {

    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);

    Optional<PersonalHistory> findById(Long historyId);

    PersonalHistory save(PersonalHistory personalHistory);

    @Query("SELECT SUM(ph.amount) FROM PersonalHistory ph WHERE ph.userId = :userId AND ph.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> findTotalAmountByDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
