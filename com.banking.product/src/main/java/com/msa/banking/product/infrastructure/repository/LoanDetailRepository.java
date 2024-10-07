package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.domain.model.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanDetailRepository extends JpaRepository<LoanDetail, UUID> {
    boolean existsByPdfInfoId(Long pdfInfoId);
}
