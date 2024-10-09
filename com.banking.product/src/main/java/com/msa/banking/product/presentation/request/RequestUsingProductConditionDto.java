package com.msa.banking.product.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUsingProductConditionDto {
    private UUID userid;
    private String name;// 직원만 가능

}
