package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.notification.dto.response.NotificationListRes;
import org.accompany.backend.domain.notification.dto.response.NotificationRes;
import org.accompany.backend.domain.notification.dto.response.NotificationTestRes;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmSendService fcmSendService;

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
                        n.getUserProcedureChecklist().getUserProcedureChecklistId(),
                        n.getDeceasedProfile().getDeceasedProfileId(),
                        n.getDeceasedProfile().getName(),
                        n.getUserProcedureChecklist().getDueDate()
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

    @Override
    public NotificationTestRes sendTestNotification(Long userId) {
        log.info("[Notification] 테스트 발송 시작 - userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new BusinessException(ErrorCode.NOTIFICATION_FCM_TOKEN_NOT_REGISTERED);
        }

        List<DeceasedProfile> profiles = user.getDeceasedProfiles();
        if (profiles.isEmpty()) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        List<NotificationTestRes.TestResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (DeceasedProfile profile : profiles) {
            FcmMessagePayload payload = new FcmMessagePayload(
                    profile.getName(),
                    "FCM 발송 테스트입니다",
                    0L
            );

            FcmSendResult result = fcmSendService.send(fcmToken, payload);

            if (result.success()) {
                successCount++;
            } else {
                failureCount++;
            }

            results.add(new NotificationTestRes.TestResult(
                    profile.getDeceasedProfileId(),
                    profile.getName(),
                    result.success(),
                    result.messageId(),
                    result.failureReason()
            ));
        }

        log.info("[Notification] 테스트 발송 완료 - userId={}, total={}, success={}, failure={}",
                userId, profiles.size(), successCount, failureCount);

        return new NotificationTestRes(
                profiles.size(),
                successCount,
                failureCount,
                results
        );
    }
}
