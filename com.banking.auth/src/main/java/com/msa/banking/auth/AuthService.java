package com.msa.banking.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    public void testRequest() {
        String txt = authClient.test();
    }
}
