package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.notification.dto.response.NotificationListRes;
import org.accompany.backend.domain.notification.dto.response.NotificationRes;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationListRes getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        List<NotificationRes> notificationResList = notifications.stream()
                .map(n -> new NotificationRes(
                        n.getNotificationId(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt(),
                        n.getUserProcedureChecklist().getUserProcedureChecklistId()
                ))
                .toList();

        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        return new NotificationListRes(notificationResList, unreadCount);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        notification.markAsRead();
    }
}
