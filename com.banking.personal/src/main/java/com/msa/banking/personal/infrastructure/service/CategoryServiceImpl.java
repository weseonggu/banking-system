package com.msa.banking.personal.infrastructure.service;

import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
import com.msa.banking.personal.application.dto.category.CategoryListDto;
import com.msa.banking.personal.application.service.CategoryService;
import com.msa.banking.personal.domain.model.Category;
import com.msa.banking.personal.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 본인이 생성한 카테고리 목록 조회
     */
    @Override
    public List<CategoryListDto> getCategoryList(UUID userId) {

        List<Category> categoryList = categoryRepository.findAllByUserId(userId);

        if(!categoryList.isEmpty()){
            return categoryList.stream().map(CategoryListDto::toDTO).toList();
        } else{
            throw new GlobalCustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    /**
     * 카테고리 생성
     */
    @Override
    public Category createCategory(String categoryName, UUID userId) {

        Category category = Category.createCategory(categoryName, userId);
        return categoryRepository.save(category);
    }

    /**
     * 카테고리 이름 조회(중복체크로 사용)
     */
    @Override
    public Optional<Category> findByName(String categoryName, UUID userId) {

        return categoryRepository.findByNameAndUserId(categoryName, userId);
    }
}
