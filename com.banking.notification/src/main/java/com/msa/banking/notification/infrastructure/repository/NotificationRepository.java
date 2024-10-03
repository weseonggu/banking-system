package com.msa.banking.notification.infrastructure.repository;

import com.msa.banking.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
