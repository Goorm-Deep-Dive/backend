package org.accompany.backend.domain.notification.dto.response;

import java.time.LocalDateTime;

public record NotificationRes(
        Long notificationId,
        String message,
        boolean isRead,
        LocalDateTime createdAt,
        Long userProcedureChecklistId,
        Long deceasedProfileId,
        String deceasedName,
        LocalDateTime dueDate
) {
}
