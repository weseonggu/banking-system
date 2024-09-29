package com.msa.banking.personal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/personal/test")
    public String test() {
        return "test";
    }
}
