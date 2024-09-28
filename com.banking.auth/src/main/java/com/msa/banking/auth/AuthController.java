package com.msa.banking.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping("/auth/start")
    public String startTest(){
        authService.testRequest();
        return "ok";
    }

    @GetMapping("/auth/test")
    public String test(@RequestHeader("X-User-id") String userid,
                       @RequestHeader("X-User-Username") String username,
                       @RequestHeader("X-User-Role") String role ){
        log.info(userid + " " + username + " " + role);
        return "ok";
    }
}
