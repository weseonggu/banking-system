package com.msa.banking.commonbean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TestBean {

    public String testCode(){
        return "test";
    }
}
