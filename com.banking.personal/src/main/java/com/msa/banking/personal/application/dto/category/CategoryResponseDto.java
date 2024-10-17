package com.msa.banking.personal.application.dto.category;

import com.msa.banking.personal.domain.model.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDto {

    String categoryName;

    public static CategoryResponseDto toDTO(Category category) {
        return CategoryResponseDto.builder()
                .categoryName(category.getName())
                .build();
    }
}
