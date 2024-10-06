package com.msa.banking.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    SIGN_UP("notification-signUp"),
    SIGN_UP_FAILED("auth-signUp-fail"),
    PERFORMANCE_MASTER_SLACK_LIST("performance-master-slack-list"),
    TRANSFER("notification-transfer");

    private final String topic;

}
