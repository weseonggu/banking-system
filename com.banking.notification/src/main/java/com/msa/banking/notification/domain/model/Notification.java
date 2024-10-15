package com.msa.banking.notification.domain.model;

import com.msa.banking.common.base.AuditEntity;
import com.msa.banking.common.base.UserRole;
import com.msa.banking.common.notification.NotiType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_delete = false")
@Getter
public class Notification extends AuditEntity {

    @Id
    @UuidGenerator
    @Column(name = "notification_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotiType type;

    @Lob
    @Column(nullable = false)
    private String message;

    public Notification(UUID userId, String slackId, UserRole role, NotiType type, String message) {
        this.userId = userId;
        this.slackId = slackId;
        this.role = role;
        this.type = type;
        this.message = message;
    }

    public static Notification createNotification(UUID userId, String slackId, UserRole role, NotiType type, String message) {
        return new Notification(userId, slackId, role, type, message);
    }
}
