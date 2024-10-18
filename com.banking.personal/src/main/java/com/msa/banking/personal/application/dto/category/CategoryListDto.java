package com.msa.banking.personal.application.dto.category;

import com.msa.banking.personal.domain.model.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryListDto {

    String categoryName;

    public static CategoryListDto toDTO(Category category) {
        return CategoryListDto.builder()
                .categoryName(category.getName())
                .build();
    }
}
