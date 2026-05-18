package org.accompany.backend.domain.notification.event;

public record CategoryCompletedEvent(
        Long profileId,
        Long categoryId,
        String categoryName,
        Long lastCheckedChecklistId
) {
}
