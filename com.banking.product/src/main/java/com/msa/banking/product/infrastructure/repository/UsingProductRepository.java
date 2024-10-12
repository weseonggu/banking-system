package com.msa.banking.product.infrastructure.repository;

import com.msa.banking.product.domain.model.UsingProduct;
import com.msa.banking.product.domain.repository.UsingProductRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsingProductRepository extends JpaRepository<UsingProduct, UUID>, UsingProductRepositoryCustom {

    @Query("SELECT u FROM UsingProduct u WHERE u.loanInUse IS NOT NULL AND u.createdAt BETWEEN :startDateTime AND :endDateTime AND u.isDelete = false")
    List<UsingProduct> findByLoanInUseIsNotNullAndCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime,
                                                                   @Param("endDateTime") LocalDateTime endDateTime);

    Optional<UsingProduct> findByAccountIdAndIsDeleteFalse(UUID accountId);

    @EntityGraph(attributePaths = {"loanInUse"})
    @Query("select u FROM UsingProduct  u WHERE u.id = :id")
    Optional<UsingProduct> findByIdEntityGraph(@Param("id")UUID id);


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UsingProduct p WHERE p.userId = :userId AND p.productId = :productId AND p.isUsing = :isUsing")
    boolean existsByUserIdAndProductIdAndIsUsing(@Param("userId") UUID userId,
                                                 @Param("productId") UUID productId,
                                                 @Param("isUsing") boolean b);
}
