package com.msa.banking.personal.application.event;

import com.msa.banking.common.notification.NotificationRequestDto;

public interface EventProducer {

    void sendBudgetOverRunNotification(NotificationRequestDto notificationRequestDto);
}
