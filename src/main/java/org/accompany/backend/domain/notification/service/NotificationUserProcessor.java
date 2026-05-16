package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.service.ChecklistServiceImpl;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.entity.NotificationDeliveryStatus;
import org.accompany.backend.domain.notification.repository.NotificationBulkRepository;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationUserProcessor {

    private final NotificationRepository notificationRepository;
    private final NotificationBulkRepository notificationBulkRepository;
    private final ChecklistServiceImpl checklistService;
    private final FcmSendService fcmSendService;

    @Transactional
    public void process(User user, List<UserProcedureChecklist> notificationTargetChecklists, LocalDate today) {
        log.info("[NotificationUserProcessor] 시작 - userId={}", user.getUserId());

        Map<Long, List<UserProcedureChecklist>> byProfile = notificationTargetChecklists.stream()
                .collect(Collectors.groupingBy(c -> c.getDeceasedProfile().getDeceasedProfileId()));

        List<Notification> toInsert = new ArrayList<>();

        for (Map.Entry<Long, List<UserProcedureChecklist>> entry : byProfile.entrySet()) {
            Long profileId = entry.getKey();
            List<UserProcedureChecklist> profileChecklists = entry.getValue();

            String idempotencyKey = buildIdempotencyKey(profileId, today);

            Optional<UserProcedureChecklist> closestOpt = profileChecklists.stream()
                    .min(Comparator
                            .comparing(UserProcedureChecklist::getDueDate)
                            .thenComparing(c -> c.getProcedure().getProcedureId()));

            if (closestOpt.isEmpty()) continue;

            UserProcedureChecklist closest = closestOpt.get();
            Integer daysLeft = checklistService.calculateRemainingDays(closest.getDueDate());

            String message;
            if (daysLeft == 0) {
                message = "가장 빠른 기한이 D-day에요";
            } else {
                message = "가장 빠른 기한까지 D-" + daysLeft + "일 남았어요";
            }

            Notification notification = Notification.builder()
                    .user(user)
                    .deceasedProfile(closest.getDeceasedProfile())
                    .userProcedureChecklist(closest)
                    .message(message)
                    .isRead(false)
                    .idempotencyKey(idempotencyKey)
                    .build();

            toInsert.add(notification);
        }

        if (toInsert.isEmpty()) {
            log.info("[NotificationUserProcessor] 대상 없음 - userId={}", user.getUserId());
            return;
        }

        notificationBulkRepository.bulkInsert(toInsert);

        // bulk insert 후 PENDING 알림들을 영속성 컨텍스트로 가져옴 (dirty checking 활용)
        List<String> idempotencyKeys = toInsert.stream()
                .map(Notification::getIdempotencyKey)
                .toList();

        List<Notification> pendingNotifications = notificationRepository
                .findByIdempotencyKeysAndStatus(idempotencyKeys, NotificationDeliveryStatus.PENDING);

        // FCM 발송
        int successCount = 0;
        int failureCount = 0;

        String fcmToken = user.getFcmToken();
        boolean hasToken = fcmToken != null && !fcmToken.isBlank();

        for (Notification notification : pendingNotifications) {
            if (!hasToken) {
                notification.markAsFailed("FCM 토큰 미등록");
                failureCount++;
                continue;
            }

            FcmMessagePayload payload = new FcmMessagePayload(
                    notification.getDeceasedProfile().getName(),
                    notification.getMessage(),
                    notification.getNotificationId()
            );

            FcmSendResult result = fcmSendService.send(fcmToken, payload);

            if (result.success()) {
                notification.markAsSent();
                successCount++;
            } else {
                notification.markAsFailed(result.failureReason());
                failureCount++;
            }
        }

        log.info("[NotificationUserProcessor] 완료 - userId={}, 생성={}, 발송성공={}, 발송실패={}",
                user.getUserId(), toInsert.size(), successCount, failureCount);
    }

    private String buildIdempotencyKey(Long profileId, LocalDate today) {
        return "DDAY-" + profileId + "-" + today.toString();
    }
}
