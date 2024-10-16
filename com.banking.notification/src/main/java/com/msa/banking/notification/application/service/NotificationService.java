package com.msa.banking.notification.application.service;

import com.msa.banking.common.auth.dto.SlackIdRequestDto;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.common.response.ErrorCode;
import com.msa.banking.commonbean.exception.GlobalCustomException;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    @Value("${slack.botKey}")
    private String slackToken;

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    /**
     * 알림 DB 저장 및 슬랙 메세지 전송
     * @param request
     * @throws SlackApiException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Transactional
    @CircuitBreaker(name = "slackService", fallbackMethod = "sendMessageFallback")
    public void creatednotify(NotificationRequestDto request) throws SlackApiException, IOException, URISyntaxException {

        // Notification 엔티티 변환
        Notification notification = Notification.createNotification(request.getUserId(), request.getSlackId(), request.getRole(), request.getType(), request.getMessage());

        // DB 저장
        Notification save = notificationRepository.save(notification);

        // 슬랙 메세지 전송
        try {
            sendMessage(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 슬랙 봇으로 DM 보내기
     * @throws IOException
     * @throws SlackApiException
     */
    public void sendMessage(NotificationRequestDto requestDto) throws IOException, SlackApiException, URISyntaxException {
        log.info("슬랙 메세지 전송 시도 중");
        Slack slack = Slack.getInstance();

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .token(slackToken)
                .channel(requestDto.getSlackId())
                .text(requestDto.getMessage())
                .build();

        ChatPostMessageResponse response = slack.methods().chatPostMessage(request);
        if (response.isOk()) {
            log.info("Message sent successfully to user: " + requestDto.getSlackId());
        } else {
            log.error("Error sending message: " + response.getError());
            throw new RuntimeException(response.getError());
        }
        log.info("슬랙 메세지 전송 완료");
    }

    /**
     * 슬랙 메세지 전송 실패 시 회원가입 롤백 메서드
     * @param requestDto
     * @param throwable
     */
    public void sendMessageFallback(NotificationRequestDto requestDto, Throwable throwable) {
        log.error("Slack 메시지 전송 실패. 서킷이 열렸습니다. 에러 메시지: {}", throwable.getMessage());
        requestDto.setMessage(requestDto.getSlackId() + " 슬랙 ID 로 회원가입 알림 전송 실패");

        kafkaTemplate.send(Topic.SIGN_UP_FAILED.getTopic(), EventSerializer.serialize(requestDto));
    }

    /**
     * 슬랙 인증번호 발송
     * @throws IOException
     * @throws SlackApiException
     */
    public String sendMessage(SlackIdRequestDto requestDto) throws IOException, SlackApiException, URISyntaxException {
        log.info("슬랙 인증번호 전송 시도 중");

        // 6자리 랜덤 인증번호 생성
        Random random = new Random();
        int randomCode = 100000 + random.nextInt(900000); // 100000 ~ 999999 사이의 숫자

        Slack slack = Slack.getInstance();

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .token(slackToken)
                .channel(requestDto.getSlackId())
                .text(String.valueOf(randomCode))
                .build();

        ChatPostMessageResponse response = slack.methods().chatPostMessage(request);
        if (response.isOk()) {
            log.info("Message sent successfully to user: " + requestDto.getSlackId());
        } else {
            log.error("Error sending message: " + response.getError());
            throw new GlobalCustomException(ErrorCode.SLACK_ERROR);
        }
        log.info("슬랙 인증번호 전송 시도 완료");
        return String.valueOf(randomCode);
    }
}
