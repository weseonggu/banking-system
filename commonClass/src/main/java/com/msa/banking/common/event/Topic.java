package com.msa.banking.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    SIGN_UP("notification-signUp"),
    SIGN_UP_FAILED("auth-signUp-fail"),
    PERFORMANCE_MASTER_SLACK_LIST("performance-master-slack-list"),
    PERFORMANCE_MASTER_SLACK_LIST_LOAN_LIST("performance-master-slack-list-loan-list"),
    PERFORMANCE_MASTER_SLACK_LIST_LOAN_COUNT_TOTAL_AMOUNT("performance-master-slack-list-loan-total-amount"),
    PERFORMANCE_PRODUCT_COUNT("performance-product-count"),
    TRANSFER("notification-transfer");

    private final String topic;

}
