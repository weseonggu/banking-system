package com.msa.banking.product.application.dto;

import com.msa.banking.product.lib.ProductType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UsingProductPage {
    private UUID id;
    private UUID userId;
    private LocalDateTime subscriptionDate;
    private ProductType type;
    private String name;
    private UUID productId;

    @QueryProjection
    public UsingProductPage(UUID id, UUID userId, LocalDateTime subscriptionDate, ProductType type, String name, UUID productId) {
        this.id = id;
        this.userId = userId;
        this.subscriptionDate = subscriptionDate;
        this.type = type;
        this.name = name;
        this.productId = productId;
    }
}
