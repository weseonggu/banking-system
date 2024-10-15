package com.msa.banking.product.lib;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    USING("실행 중"),
    CANCLE("해지"),
    STOP("휴면 중");

    private final String value;
    SubscriptionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
