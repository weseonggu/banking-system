package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.domain.model.PDFInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFInfoRepository extends JpaRepository<PDFInfo, Long> {
//    @EntityGraph
}
