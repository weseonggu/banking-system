package com.msa.banking.product.infrastructure.scheduler;

import com.msa.banking.common.event.EventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PerformanceScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    //TODO 대출가입건수, 예금 가입건수(없을 수 있음) count() 조회하여 performance 로 전송 (DTO 변환 후 전송)
    @KafkaListener(topics = "performance-master-slack-list", groupId = "AuthService-group")
    public void listen(String message) {

        List<?> request = EventSerializer.deserialize(message, List.class);


    }
}
