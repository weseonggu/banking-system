package com.msa.banking.account.application.event;

import com.msa.banking.common.personal.PersonalHistoryRequestDto;

public interface EventProducer {

    void sendTransactionCreatedEvent(PersonalHistoryRequestDto personalHistoryRequestDto);
}
