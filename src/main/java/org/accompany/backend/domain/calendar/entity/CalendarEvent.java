package org.accompany.backend.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calendarEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deceased_profile_id")
    private DeceasedProfile deceasedProfile;

    /**
     * 체크리스트 기반 일정이면 연결
     * 일반 일정/구글 일정이면 null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_procedure_checklist_id")
    private UserProcedureChecklist userProcedureChecklist;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventType eventType;

    @Column(length = 255)
    private String googleCalendarId;

    @Column(length = 255)
    private String googleEventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus syncStatus;

    private LocalDateTime lastSyncedAt;

    @Builder
    public CalendarEvent(
            User user,
            DeceasedProfile deceasedProfile,
            UserProcedureChecklist userProcedureChecklist,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            EventType eventType,
            String googleCalendarId,
            String googleEventId,
            SyncStatus syncStatus,
            LocalDateTime lastSyncedAt
    ) {
        this.user = user;
        this.deceasedProfile = deceasedProfile;
        this.userProcedureChecklist = userProcedureChecklist;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.eventType = eventType;
        this.googleCalendarId = googleCalendarId;
        this.googleEventId = googleEventId;

        this.syncStatus = syncStatus != null
                ? syncStatus
                : SyncStatus.PENDING;

        this.lastSyncedAt = lastSyncedAt; // LocalDateTime.now();
    }

    /**
     * 일정 수정
     */
    public void updateSchedule(
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    /**
     * 구글 동기화 정보 수정
     */
    public void updateGoogleSyncInfo(
            String googleCalendarId,
            String googleEventId
    ) {
        this.googleCalendarId = googleCalendarId;
        this.googleEventId = googleEventId;
        this.lastSyncedAt = LocalDateTime.now();
    }

    /**
     * 구글 sync 성공
     */
    public void markSynced() {
        this.syncStatus = SyncStatus.SYNCED;
        this.lastSyncedAt = LocalDateTime.now();
    }

    /**
     * 구글 sync 실패
     */
    public void markSyncFailed() {
        this.syncStatus = SyncStatus.FAILED;
    }

    /**
     * 구글 sync 대기
     */
    public void markSyncPending() {
        this.syncStatus = SyncStatus.PENDING;
    }
}
