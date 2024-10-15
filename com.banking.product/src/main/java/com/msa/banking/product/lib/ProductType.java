package com.msa.banking.product.lib;

import lombok.Getter;

@Getter
public enum ProductType {
    CHECKING("입출금 상품"),
    NEGATIVE_LOANS("대출 상품");

    private final String value;
    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
