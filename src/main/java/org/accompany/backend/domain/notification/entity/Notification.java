package org.accompany.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.user.entity.User;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_procedure_checklist_id", nullable = false)
    private UserProcedureChecklist userProcedureChecklist;

    @Column(length = 500)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(unique = true, nullable = false, length = 36)
    private String idempotencyKey;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationDeliveryStatus deliveryStatus;

    private LocalDateTime sentAt;

    @Column(length = 500)
    private String failureReason;

    @Builder
    public Notification(
            User user,
            UserProcedureChecklist userProcedureChecklist,
            String message,
            boolean isRead
    ) {
        this.user = user;
        this.userProcedureChecklist = userProcedureChecklist;
        this.message = message;
        this.isRead = isRead;
        this.idempotencyKey = UUID.randomUUID().toString();
        this.deliveryStatus = NotificationDeliveryStatus.PENDING;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsUnread() {
        this.isRead = false;
    }

    public void markAsSent() {
        this.deliveryStatus = NotificationDeliveryStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.deliveryStatus = NotificationDeliveryStatus.FAILED;
        this.failureReason = reason;
    }
}
