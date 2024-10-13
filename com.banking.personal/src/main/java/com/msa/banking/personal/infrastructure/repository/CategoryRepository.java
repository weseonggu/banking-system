package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.domain.repository.CategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>, CategoryRepositoryCustom {
    Optional<Category> findByName(String categoryName);
}
