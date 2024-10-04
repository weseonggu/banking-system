package com.msa.banking.auth.infrastructure.messaging;

import com.msa.banking.auth.application.service.UserService;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.notification.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topics = "auth-signUp-fail", groupId = "Notification-group")
    public void listen(String message) {
        log.error("회원 가입 메시징 실패 롤백 처리 시도 중 | message: {}", message);
        NotificationRequestDto request = EventSerializer.deserialize(message, NotificationRequestDto.class);

        userService.rollbackSignUp(request);
        log.error("회원 가입 메시징 실패 롤백 완료");
    }
}
