package com.msa.banking.common.product.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UsingProductFeignResponseDto {

    private UUID id;
    private UUID userId;
    private UUID accountId;
}
