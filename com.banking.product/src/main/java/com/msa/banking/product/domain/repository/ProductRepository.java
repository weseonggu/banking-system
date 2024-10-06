package com.msa.banking.product.domain.repository;

import com.msa.banking.product.domain.model.Product;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {

//    @EntityGraph(attributePaths = {
//            "checkingDetail",       // CheckingDetail 연관 엔티티
//            "checkingDetail.pdfInfo",  // CheckingDetail과 연관된 PDFInfo
//            "loanDetail",           // LoanDetail 연관 엔티티
//            "loanDetail.pdfInfo"     // LoanDetail과 연관된 PDFInfo
//    })
    @Query("SELECT p FROM Product p "
        + "LEFT JOIN FETCH p.checkingDetail cd "
        + "LEFT JOIN FETCH cd.pdfInfo "
        + "LEFT JOIN FETCH p.loanDetail ld "
        + "LEFT JOIN FETCH ld.pdfInfo "
        + "WHERE p.id = :productId")
    Optional<Product> findEntityGrapById(@Param("productId")UUID productId);
}
