package com.msa.banking.personal.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false)
    private UUID id;

    // TODO ENUM으로 변경할 지
    @Column(name = "name", nullable = false)
    private String name;

    // 본인이 생성한 카테고리를 알기위해 userId 추가
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    public static Category createCategory(String categoryName, UUID userId){
        return Category.builder()
                .name(categoryName)
                .userId(userId)
                .build();
    }
}
