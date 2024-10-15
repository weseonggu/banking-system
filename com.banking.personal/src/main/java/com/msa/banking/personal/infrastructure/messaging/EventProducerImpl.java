package com.msa.banking.personal.infrastructure.messaging;

import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.common.event.Topic;
import com.msa.banking.common.notification.NotificationRequestDto;
import com.msa.banking.personal.application.event.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventProducerImpl implements EventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final String TOPIC = Topic.NOTIFICATION_BUDGET_OVER_RUN.getTopic();

    @Override
    public void sendBudgetOverRunNotification(NotificationRequestDto notificationRequestDto) {

        try {
            byte[] message = EventSerializer.serialize(notificationRequestDto);
            kafkaTemplate.send(TOPIC, message);
            log.info("Notification sent to Kafka topic: " + TOPIC);

        } catch (Exception e) {
            log.error("Failed to send notification to Kafka: " + e.getMessage());
        }
    }
}
