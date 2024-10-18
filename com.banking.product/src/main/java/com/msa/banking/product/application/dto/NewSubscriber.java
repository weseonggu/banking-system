package com.msa.banking.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewSubscriber {
    private String name;
    private UUID account_id;
    private UUID user_id;
    private UUID using_product;
}
