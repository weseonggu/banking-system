package com.msa.banking.notification.application.service;

import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.notification.domain.model.Notification;
import com.msa.banking.notification.infrastructure.repository.NotificationRepository;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    @Value("${slack.botKey}")
    private String slackToken;

    private final NotificationRepository notificationRepository;

    /**
     * 알림 DB 저장 및 슬랙 메세지 전송
     * @param request
     * @throws SlackApiException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Transactional
    public void creatednotify(NotificationRequestDto request) throws SlackApiException, IOException, URISyntaxException {

        Notification notification = Notification.createNotification(request.getUserId(), request.getSlackId(), request.getRole(), request.getType(), request.getMessage());

        // DB 저장
        notificationRepository.save(notification);

        // 슬랙 메세지 전송
        sendMessage(request.getMessage(), request.getSlackId());
    }

    /**
     * 슬랙 봇으로 DM 보내기
     * @throws IOException
     * @throws SlackApiException
     */
    @CircuitBreaker(name = "slackCircuitBreaker", fallbackMethod = "sendMessageFallback")
    public void sendMessage(String message, String... slackId) throws IOException, SlackApiException, URISyntaxException {
        Slack slack = Slack.getInstance();

        for (String slackUserId : slackId) {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .token(slackToken)
                    .channel(slackUserId)
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slack.methods().chatPostMessage(request);
            if (response.isOk()) {
                log.info("Message sent successfully to user: " + slackUserId);
            } else {
                log.error("Error sending message: " + response.getError());
                throw new RuntimeException(response.getError());
            }
        }
    }

    // 재시도 및 회로 차단 실패 시 호출되는 폴백 메서드
    public void sendMessageFallback(String message, Throwable t, String... slackId) {
        log.error("Slack message sending failed after retries and circuit breaker activation. Fallback executed.", t);
        throw new RuntimeException("Failed to send Slack message after retries and fallback", t);
    }
}
