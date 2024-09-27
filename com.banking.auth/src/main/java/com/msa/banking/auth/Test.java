package com.msa.banking.auth;

import com.msa.banking.common.TestDto;
import com.msa.banking.commonbean.TestBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Test {

    private final TestBean testBean;

    public void test() {
        testBean.testCode();
        TestDto dto = new TestDto();
        dto.getTest();
    }
}
