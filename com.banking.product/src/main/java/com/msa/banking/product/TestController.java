package com.msa.banking.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/product/test")
    public String test() {
        return "test";
    }
}
