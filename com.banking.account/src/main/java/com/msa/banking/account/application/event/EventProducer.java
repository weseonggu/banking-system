package com.msa.banking.account.application.event;

import com.msa.banking.account.domain.model.AccountTransactions;
import com.msa.banking.common.personal.PersonalHistoryRequestDto;

import java.util.UUID;

public interface EventProducer {

    void sendTransactionCreatedEvent(UUID accountId, UUID userId, String role, AccountTransactions withdrawalTransaction);
}