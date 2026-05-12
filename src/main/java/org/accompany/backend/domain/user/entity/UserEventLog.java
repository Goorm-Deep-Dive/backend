package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_event_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userEventLogId;

    @Column(length = 255)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Provider provider;

    @Column(length = 255)
    private String failureReason;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private UserEventLog(String deviceId, User user, UserEventType eventType, Provider provider, String failureReason) {
        this.deviceId = deviceId;
        this.user = user;
        this.eventType = eventType;
        this.provider = provider;
        this.failureReason = failureReason;
    }
}
