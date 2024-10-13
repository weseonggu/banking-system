package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.*;
import com.msa.banking.product.lib.ProductType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CheckingInUseDetailDto extends UsingProductDetailDto {

    private UUID id;
    private LocalDateTime subscriptionDate;
    private String type;
    private String name;
    private UUID productId;
    private Boolean isUsing;

    private BigDecimal interestRate;
    private Boolean feeWaiver;

    public static CheckingInUseDetailDto of (UsingProduct product) {
        CheckingInUse inUse = product.getCheckingInUse();
        return CheckingInUseDetailDto.builder()
                .id(product.getId())
                .subscriptionDate(product.getSubscriptionDate())
                .type(product.getType().getValue())
                .name(product.getName())
                .productId(product.getProductId())
                .isUsing(product.getIsUsing())
                .interestRate(inUse.getInterestRate())
                .feeWaiver(inUse.getFeeWaiver())
                .build();
    }

}
