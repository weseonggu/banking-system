package com.msa.banking.notification.presentation.controller;

import com.msa.banking.common.auth.dto.SlackIdRequestDto;
import com.msa.banking.notification.application.service.NotificationService;
import com.slack.api.methods.SlackApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/slack-code")
    public String slackCheck(@RequestBody SlackIdRequestDto request) throws SlackApiException, IOException, URISyntaxException {
        log.info("슬랙 ID 인증번호 발급 시도 중 | slackId: {}", request.getSlackId());

        String response = notificationService.sendMessage(request);

        log.info("슬랙 ID 인증번호 발급 완료 | slackId: {}", request.getSlackId());
        return response;
    }
}
