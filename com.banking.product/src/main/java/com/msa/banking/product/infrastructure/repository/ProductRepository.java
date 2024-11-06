package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.ProductRepositoryCustom;
import com.msa.banking.product.lib.ProductType;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {
            "checkingDetail",       // CheckingDetail 연관 엔티티
            "checkingDetail.pdfInfo",  // CheckingDetail과 연관된 PDFInfo
            "loanDetail",           // LoanDetail 연관 엔티티
            "loanDetail.pdfInfo",
            "productLike"// LoanDetail과 연관된 PDFInfo
    })
    @Query("SELECT p FROM Product p WHERE p.id = :productId and p.isDelete = false")
    Optional<Product> findEntityGrapById(@Param("productId")UUID productId);


    @EntityGraph(attributePaths = {
            "checkingDetail",       // CheckingDetail 연관 엔티티
            "checkingDetail.pdfInfo",  // CheckingDetail과 연관된 PDFInfo
            "loanDetail",           // LoanDetail 연관 엔티티
            "loanDetail.pdfInfo"     // LoanDetail과 연관된 PDFInfo
    })
    @Query("SELECT p FROM Product p " +
            "WHERE p.id = :productId " +
            "AND p.isDelete = :deleted " +
            "AND :current_time BETWEEN p.validFrom AND p.validTo "+
            "AND p.type = :type")
    Optional<Product> findByIdWhereIsDeleted(@Param("productId") UUID productId, @Param("deleted") Boolean deleted, @Param("type") ProductType type, @Param("current_time") LocalDateTime current_time);

    @EntityGraph(attributePaths = {
            "productLike"
    })
    Optional<Product> findByIdAndIsDeleteFalse(UUID productId);
}
