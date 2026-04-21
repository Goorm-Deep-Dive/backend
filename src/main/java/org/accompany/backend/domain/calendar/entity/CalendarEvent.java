package org.accompany.backend.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
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

    @Column(length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Column(length = 255)
    private String googleCalendarId;

    @Column(length = 255)
    private String googleEventId;

    @Builder
    public CalendarEvent(
            User user,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String googleCalendarId,
            String googleEventId
    ) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.googleCalendarId = googleCalendarId;
        this.googleEventId = googleEventId;
    }

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

    public void updateGoogleSyncInfo(String googleCalendarId, String googleEventId) {
        this.googleCalendarId = googleCalendarId;
        this.googleEventId = googleEventId;
    }
}
