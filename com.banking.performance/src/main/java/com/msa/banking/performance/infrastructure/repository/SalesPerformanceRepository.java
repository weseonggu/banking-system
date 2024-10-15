package com.msa.banking.performance.infrastructure.repository;

import com.msa.banking.performance.domain.model.SalesPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesPerformanceRepository extends JpaRepository<SalesPerformance, UUID> {
}
