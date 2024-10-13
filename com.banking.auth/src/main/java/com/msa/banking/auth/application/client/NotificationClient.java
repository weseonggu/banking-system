package com.msa.banking.auth.application.client;

import com.msa.banking.common.auth.dto.SlackIdRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NotificationService")
public interface NotificationClient {

    @PostMapping("/api/notifications/slack-code")
    String slackCheck(@RequestBody SlackIdRequestDto request);

}
