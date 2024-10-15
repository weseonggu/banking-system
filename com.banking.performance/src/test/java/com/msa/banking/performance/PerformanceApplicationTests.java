package com.msa.banking.performance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "SLACK_API_TOKEN=my-temporary-token",
        "eureka.client.enabled=false",
        "spring.cloud.config.fail-fast=false"
})
@ActiveProfiles("dev")
class PerformanceApplicationTests {

    @Test
    void contextLoads() {
    }

}
