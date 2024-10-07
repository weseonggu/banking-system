package com.msa.banking.product.application.dto;

import com.msa.banking.product.lib.ProductType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResponseProductPage implements Serializable {
    private UUID id;
    private String name;
    private ProductType type;

    @QueryProjection
    public ResponseProductPage(UUID id, String name, ProductType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
