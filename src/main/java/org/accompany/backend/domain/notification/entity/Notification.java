package org.accompany.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.user.entity.User;

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
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsUnread() {
        this.isRead = false;
    }
}
