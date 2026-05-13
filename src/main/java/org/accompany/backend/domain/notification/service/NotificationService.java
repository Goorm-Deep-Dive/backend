package org.accompany.backend.domain.notification.service;

import org.accompany.backend.domain.notification.dto.response.NotificationListRes;

public interface NotificationService {

    NotificationListRes getNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);

}
