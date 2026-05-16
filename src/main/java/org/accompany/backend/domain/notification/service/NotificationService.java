package org.accompany.backend.domain.notification.service;

import org.accompany.backend.domain.notification.dto.response.NotificationListRes;
import org.accompany.backend.domain.notification.dto.response.NotificationTestRes;

public interface NotificationService {

    NotificationListRes getNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);

    NotificationTestRes sendTestNotification(Long userId);

}
