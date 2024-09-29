package com.msa.banking.account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/account/test")
    public String test() {
        return "test";
    }
}
