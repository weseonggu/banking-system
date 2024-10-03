package com.msa.banking.common.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotiType {

    BUDGET_OVERRUN(Notify.BUDGET_OVERRUN),
    TRANSFER(Notify.TRANSFER),
    SIGNUP(Notify.SIGNUP);

    private final String notify;

    public static class Notify {
        public static final String BUDGET_OVERRUN = "BUDGET_OVERRUN";
        public static final String TRANSFER = "TRANSFER";
        public static final String SIGNUP = "SIGNUP";
    }
}
