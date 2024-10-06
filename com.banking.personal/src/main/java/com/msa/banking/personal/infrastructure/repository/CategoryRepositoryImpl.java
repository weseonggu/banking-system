package com.msa.banking.personal.infrastructure.repository;

import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.domain.repository.CategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepositoryImpl extends JpaRepository<Category, UUID>, CategoryRepository {
    @Override
    Optional<Category> findByName(String categoryName);
}
