package com.msa.banking.personal.application.event;

public interface EventConsumer {
    void handleAccountCompletedEvent(String message);

}
