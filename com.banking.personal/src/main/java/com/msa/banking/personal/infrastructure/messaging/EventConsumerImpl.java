package com.msa.banking.personal.infrastructure.messaging;

import com.msa.banking.common.event.EventSerializer;
import com.msa.banking.personal.application.event.AccountCompletedEventDto;
import com.msa.banking.personal.application.event.EventConsumer;
import com.msa.banking.personal.application.service.PersonalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConsumerImpl implements EventConsumer {

    private final PersonalHistoryService personalHistoryService;

    @Override
    @KafkaListener(topics = "transaction-create", groupId = "personalHistory-group")
    public void handleAccountCompletedEvent(String message) {
        AccountCompletedEventDto accountCompletedEventDto = EventSerializer.deserialize(message, AccountCompletedEventDto.class);
        personalHistoryService.createPersonalHistory(accountCompletedEventDto);

    }
}