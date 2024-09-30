package com.msa.banking.common.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    MASTER(Authority.MASTER),
    MANAGER(Authority.MANAGER),
    CUSTOMER(Authority.CUSTOMER);

    private final String authority;

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String MANAGER = "MANAGER";
        public static final String CUSTOMER = "CUSTOMER";
    }
}


