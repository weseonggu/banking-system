package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.common.personal.PersonalHistoryType;
import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonalHistoryRepository extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepositoryCustom {

    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);

    @Query("SELECT SUM(ph.amount) FROM PersonalHistory ph WHERE ph.userId = :userId AND ph.transactionDate BETWEEN :startDate AND :endDate AND ph.type IN (:types)")
    Optional<BigDecimal> findTotalAmountByDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("types") List<PersonalHistoryType> types);


    @Query("SELECT c.name AS categoryName, SUM(ph.amount) AS totalSpent " +
            "FROM PersonalHistory ph " +
            "JOIN ph.category c " +  // 엔티티 관계에 따른 조인
            "WHERE ph.userId = :userId " +
            "AND ph.transactionDate BETWEEN :startDate AND :endDate " +
            "AND ph.status <> 'UNCLASSIFIED' " +  // UNCLASSIFIED 상태 제외
            "GROUP BY c.name " +
            "ORDER BY totalSpent DESC")
    List<Object[]> findMostSpentCategoryByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
