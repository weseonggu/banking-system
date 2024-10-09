package com.msa.banking.performance.infrastructure.messaging;

import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.notification.SlackIdAndLoanAndAmountDto;
import com.msa.banking.performance.application.service.SalesPerformanceService;
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
public class PerformanceConsumer {

    private final SalesPerformanceService salesPerformanceService;

    @KafkaListener(topics = "performance-master-slack-list-loan-total-amount", groupId = "AccountService-group")
    public void performance(String message) throws SlackApiException, IOException, URISyntaxException {
        // 메세지 역직렬화
        SlackIdAndLoanAndAmountDto deserialize = EventSerializer.deserialize(message, SlackIdAndLoanAndAmountDto.class);

        log.info("월별 매출 성과 기록 시도 중 | request = {}", deserialize);

        // DB 저장 및 슬랙 전송
        salesPerformanceService.performance(deserialize);

        log.info("회원가입 Kafka 메세지 슬랙 전송 완료");
    }
}
