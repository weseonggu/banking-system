package com.msa.banking.performance.infrastructure.repository;

import com.msa.banking.performance.domain.model.SalesPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SalesPerformanceRepository extends JpaRepository<SalesPerformance, UUID> {

    @Query("select sp from SalesPerformance sp where sp.evaluationMonth like :yearMonth%")
    List<SalesPerformance> findByYearMonthStartingWith(@Param("yearMonth") String yearMonth);

}
