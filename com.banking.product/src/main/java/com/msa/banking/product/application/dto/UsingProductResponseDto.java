package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.UsingProduct;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UsingProductResponseDto {

    private UUID id;
    private UUID userId;
    private UUID accountId;

    public static UsingProductResponseDto toDTO(UsingProduct usingProduct){
        return UsingProductResponseDto.builder()
                .id(usingProduct.getId())
                .userId(usingProduct.getUserId())
                .accountId(usingProduct.getAccountId())
                .build();
    }
}
