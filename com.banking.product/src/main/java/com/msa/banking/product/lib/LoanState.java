package com.msa.banking.product.lib;

import lombok.Getter;

@Getter
public enum LoanState {
    APPLY("신청"),
    RUNNING("실행 중"),
    CANCLE("해지"),
    OVERDUE("연체 중"),
    BEFOREEXECUTION("실행 전");

    private final String value;
    LoanState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
