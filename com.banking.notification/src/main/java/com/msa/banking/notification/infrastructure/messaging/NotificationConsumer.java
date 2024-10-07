package com.msa.banking.notification.infrastructure.messaging;

import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.notification.application.service.NotificationService;
import com.slack.api.methods.SlackApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-signUp", groupId = "AuthService-group")
    public void listen(String message) throws SlackApiException, IOException, URISyntaxException {
        // 메세지 역직렬화
        NotificationRequestDto request = EventSerializer.deserialize(message, NotificationRequestDto.class);

        log.info("회원가입 Kafka 메세지 슬랙 전송 시도 중 | request = {}", request);

        // DB 저장 및 슬랙 전송
        notificationService.creatednotify(request);

        log.info("회원가입 Kafka 메세지 슬랙 전송 완료");
    }

    @KafkaListener(topics = "notification-budgetOverRun", groupId = "personalHistory-group")
    public void budgetOverRunNotification(String message) throws SlackApiException, IOException, URISyntaxException {
        // 메세지 역직렬화
        NotificationRequestDto request = EventSerializer.deserialize(message, NotificationRequestDto.class);

        // DB 저장 및 슬랙 전송
        notificationService.creatednotify(request);
    }
}
