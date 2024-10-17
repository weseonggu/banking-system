package com.msa.banking.personal.application.service;

import com.msa.banking.personal.application.dto.category.CategoryListDto;
import com.msa.banking.personal.application.dto.category.CategoryResponseDto;
import com.msa.banking.personal.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {

    List<CategoryListDto> getCategoryList (UUID userId);

    Category createCategory(String categoryName, UUID userId);

    Optional<Category> findByName(String categoryName, UUID userId);
}
