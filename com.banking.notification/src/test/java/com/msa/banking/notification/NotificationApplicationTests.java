package com.msa.banking.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "SLACK_API_TOKEN=my-temporary-token"
})
@ActiveProfiles("dev")
class NotificationApplicationTests {

//    @Test
//    void contextLoads() {
//    }

}
