package com.msa.banking.product.domain.repository;

import com.msa.banking.product.domain.model.CheckingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckingDetailRepository extends JpaRepository<CheckingDetail, UUID> {
    boolean existsByPdfInfoId(Long pdfInfoId);
}
