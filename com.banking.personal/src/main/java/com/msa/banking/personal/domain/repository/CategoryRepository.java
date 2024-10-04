package com.msa.banking.personal.domain.repository;

import com.msa.banking.personal.domain.model.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(UUID categoryId);

    Optional<Category> findByName(String categoryName);

    Category save(Category category);
}
