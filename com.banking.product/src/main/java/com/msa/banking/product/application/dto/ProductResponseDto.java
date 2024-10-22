package com.msa.banking.product.application.dto;

import com.msa.banking.product.lib.ProductType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ProductResponseDto implements Serializable {
    private UUID id;
    private String name;
    private ProductType type;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isFinish;
    private ProductDetailDto detail;
}
