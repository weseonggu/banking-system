package com.msa.banking.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    SIGN_UP("notification-signUp"),
    TRANSFER("notification-transfer");

    private final String topic;

}
