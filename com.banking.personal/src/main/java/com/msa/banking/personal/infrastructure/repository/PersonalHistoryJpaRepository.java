package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.PersonalHistory;
import com.msa.banking.personal.domain.repository.PersonalHistoryRepositoryCustom;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonalHistoryJpaRepository extends JpaRepository<PersonalHistory, Long>, PersonalHistoryRepositoryCustom {

    Page<PersonalHistory> findAllByIsDeleteFalse(Pageable pageable);

    Optional<PersonalHistory> findById(Long historyId);

    PersonalHistory save(PersonalHistory personalHistory);

    @Query("SELECT ph FROM PersonalHistory ph WHERE ph.userId = :userId AND ph.transactionDate BETWEEN :startDate AND :endDate")
    List<PersonalHistory> findPersonalHistoryByDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
