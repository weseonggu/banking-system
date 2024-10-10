package com.msa.banking.account.infrastructure.messaging;

import com.msa.banking.account.application.event.EventProducer;
import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.personal.PersonalHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventProducerImpl implements EventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private static final String TOPIC = Topic.TRANSACTION_CREATE.getTopic();

    @Override
    public void sendTransactionCreatedEvent(PersonalHistoryRequestDto personalHistoryRequestDto) {

        try {
            byte[] message = EventSerializer.serialize(personalHistoryRequestDto);
            kafkaTemplate.send(TOPIC, message);
            log.info("Notification sent to Kafka topic: " + TOPIC);

        } catch (Exception e) {
            log.error("Failed to send notification to Kafka: " + e.getMessage());
        }
    }
}
