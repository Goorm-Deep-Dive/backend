package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.checklist.service.ChecklistServiceImpl;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.entity.NotificationDeliveryStatus;
import org.accompany.backend.domain.notification.entity.NotificationSlot;
import org.accompany.backend.domain.notification.repository.NotificationBulkRepository;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    public void process(User user, List<CalendarEvent> notificationTargets, LocalDate today, NotificationSlot slot) {
        log.info("[NotificationUserProcessor] 시작 - userId={}", user.getUserId());

        Map<Long, List<CalendarEvent>> byProfile = notificationTargets.stream()
                .collect(Collectors.groupingBy(ce -> ce.getUserProcedureChecklist().getDeceasedProfile().getDeceasedProfileId()));

        List<Notification> toInsert = new ArrayList<>();

        for (Map.Entry<Long, List<CalendarEvent>> entry : byProfile.entrySet()) {
            Long profileId = entry.getKey();
            List<CalendarEvent> profileEvents = entry.getValue();

            String idempotencyKey = buildIdempotencyKey(profileId, today, slot);

            Optional<CalendarEvent> closestOpt = profileEvents.stream()
                    .min(Comparator
                            .comparing(CalendarEvent::getStartAt)
                            .thenComparing(ce -> ce.getUserProcedureChecklist().getProcedure().getProcedureId()));

            if (closestOpt.isEmpty()) continue;

            CalendarEvent closest = closestOpt.get();
            Integer daysLeft = checklistService.calculateRemainingDays(closest.getStartAt());

            String message;
            if (daysLeft == 0) {
                message = "가장 빠른 기한이 D-day에요";
            } else {
                message = "가장 빠른 기한까지 D-" + daysLeft + "일 남았어요";
            }

            Notification notification = Notification.builder()
                    .user(user)
                    .deceasedProfile(closest.getUserProcedureChecklist().getDeceasedProfile())
                    .userProcedureChecklist(closest.getUserProcedureChecklist())
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

        // 1. bulk insert (자체 트랜잭션)
        notificationBulkRepository.bulkInsert(toInsert);

        // 2. PENDING 알림 조회 (별도 트랜잭션)
        List<String> idempotencyKeys = toInsert.stream()
                .map(Notification::getIdempotencyKey)
                .toList();

        List<Notification> pendingNotifications = notificationRepository
                .findByIdempotencyKeysAndStatus(idempotencyKeys, NotificationDeliveryStatus.PENDING);

        // 3. FCM 발송 (트랜잭션 외부에서 외부 네트워크 호출)
        List<Long> sentIds = new ArrayList<>();
        Map<Long, String> failedReasons = new HashMap<>();

        String fcmToken = user.getFcmToken();
        boolean hasToken = fcmToken != null && !fcmToken.isBlank();

        for (Notification notification : pendingNotifications) {
            if (!hasToken) {
                failedReasons.put(notification.getNotificationId(), "FCM 토큰 미등록");
                continue;
            }

            FcmMessagePayload payload = new FcmMessagePayload(
                    notification.getDeceasedProfile().getName(),
                    notification.getMessage(),
                    notification.getNotificationId()
            );

            FcmSendResult result = fcmSendService.send(fcmToken, payload);

            if (result.success()) {
                sentIds.add(notification.getNotificationId());
            } else {
                failedReasons.put(notification.getNotificationId(), result.failureReason());
            }
        }

        // 4. status 일괄 업데이트 (각각 자체 트랜잭션)
        notificationBulkRepository.bulkUpdateToSent(sentIds);
        notificationBulkRepository.bulkUpdateToFailed(failedReasons);

        log.info("[NotificationUserProcessor] 완료 - userId={}, 생성={}, 발송성공={}, 발송실패={}",
                user.getUserId(), toInsert.size(), sentIds.size(), failedReasons.size());
    }

    private String buildIdempotencyKey(Long profileId, LocalDate today, NotificationSlot slot) {
        return "DDAY-" + profileId + "-" + today.toString() + "-" + slot.name();
    }
}
