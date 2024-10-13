package com.msa.banking.account.infrastructure.messaging;

import com.msa.banking.account.application.event.EventProducer;
import com.msa.banking.account.application.mapper.EnumMapper;
import com.msa.banking.account.application.service.ProductService;
import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.personal.PersonalHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventProducerImpl implements EventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;  // byte[] 타입의 KafkaTemplate 사용
    private final ProductService productService;  // ProductService 주입


    private static final String TOPIC = Topic.TRANSACTION_CREATE.getTopic();

    @Override
    public void sendTransactionCreatedEvent(UUID accountId, UUID userId, String role, AccountTransactions transaction) {

        // accountId -> userId 조회
        ResponseEntity<?> responseEntity = productService.findByAccountId(transaction.getAccount().getAccountId(), userId, role);
        log.info(responseEntity.getBody());

        if (responseEntity.getBody() instanceof Map<?, ?> responseBody) {
            Object dataObject = responseBody.get("data");

            if (dataObject instanceof Map<?, ?> dataMap) {
                UUID fetchedUserId = UUID.fromString((String) dataMap.get("id"));
                log.info("UserId: " + userId);

                // PersonalHistoryRequestDto 생성
                PersonalHistoryRequestDto personalHistoryRequestDto = PersonalHistoryRequestDto.builder()
                        .userId(fetchedUserId)
                        .amount(transaction.getWithdrawalAmount())
                        .type(EnumMapper.toPersonalHistoryType(transaction.getType()))
                        .description(transaction.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .build();


                try {
                    // DTO를 직렬화하여 바이트 배열로 변환
                    byte[] message = EventSerializer.serialize(personalHistoryRequestDto);

                    // Kafka로 직렬화된 메시지 전송
                    kafkaTemplate.send(TOPIC, message);
                    log.info("Notification sent to Kafka topic: " + TOPIC);

                } catch (Exception e) {
                    log.error("Failed to send notification to Kafka: " + e.getMessage());
                }
            } else {
                log.error("Invalid data format in response body");
            }
        } else {
            log.error("Invalid response format from ProductService");
        }
    }
}