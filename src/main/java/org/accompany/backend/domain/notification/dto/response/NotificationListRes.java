package org.accompany.backend.domain.notification.dto.response;

import java.util.List;

public record NotificationListRes(
        List<NotificationRes> notifications,
        long unreadCount
) {
}
