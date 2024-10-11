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

    public static Category createCategory(String categoryName){
        return Category.builder()
                .name(categoryName)
                .build();
    }
}
