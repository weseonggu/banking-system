package com.msa.banking.product.application.dto;

import com.msa.banking.product.domain.model.*;
import com.msa.banking.product.lib.LoanState;
import com.msa.banking.product.lib.ProductType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class LoanInuseDetailDto extends UsingProductDetailDto {
    private UUID id;
    private UUID accountId;
    private LocalDateTime subscriptionDate;
    private String type;
    private String name;
    private UUID productId;
    private Boolean isUsing;

    private Long loanAmount;
    private BigDecimal interestRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reviewer;
    private LoanState status;


    public static LoanInuseDetailDto of (UsingProduct product) {
        LoanInUse inUse = product.getLoanInUse();
        return LoanInuseDetailDto.builder()
                .id(product.getId())
                .accountId(product.getAccountId())
                .subscriptionDate(product.getSubscriptionDate())
                .type(product.getType().getValue())
                .name(product.getName())
                .productId(product.getProductId())
                .isUsing(product.getIsUsing())
                .loanAmount(inUse.getLoanAmount())
                .interestRate(inUse.getInterestRate())
                .startDate(inUse.getStartDate())
                .endDate(inUse.getEndDate())
                .reviewer(inUse.getReviewer() == null ? "Under review" : inUse.getReviewer().toString())
                .status(inUse.getStatus())
                .build();
    }
}
