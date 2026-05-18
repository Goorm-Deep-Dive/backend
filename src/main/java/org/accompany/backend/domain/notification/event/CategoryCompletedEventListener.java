package org.accompany.backend.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.entity.NotificationDeliveryStatus;
import org.accompany.backend.domain.notification.repository.NotificationBulkRepository;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.notification.service.FcmMessagePayload;
import org.accompany.backend.domain.notification.service.FcmSendResult;
import org.accompany.backend.domain.notification.service.FcmSendService;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCompletedEventListener {

    private static final DateTimeFormatter HOUR_KEY_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private final UserProcedureChecklistRepository userProcedureChecklistRepository;
    private final NotificationBulkRepository notificationBulkRepository;
    private final NotificationRepository notificationRepository;
    private final FcmSendService fcmSendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CategoryCompletedEvent event) {
        log.info("[CategoryCompleted] 처리 시작 - profileId={}, categoryId={}",
                event.profileId(), event.categoryId());

        // 1. 마지막 체크된 체크리스트로부터 프로필/유저 조회
        UserProcedureChecklist checklist = userProcedureChecklistRepository
                .findById(event.lastCheckedChecklistId())
                .orElse(null);
        if (checklist == null) {
            log.warn("[CategoryCompleted] 체크리스트 없음 - id={}", event.lastCheckedChecklistId());
            return;
        }

        DeceasedProfile profile = checklist.getDeceasedProfile();
        User user = profile.getUser();

        // 2. 멱등키 + 메시지
        String idempotencyKey = "DONE-" + event.profileId() + "-" + event.categoryId()
                + "-" + LocalDateTime.now().format(HOUR_KEY_FORMAT);
        String message = event.categoryName() + " 부문 과업을 모두 완료했어요";

        // 3. Notification entity 생성 + bulk insert (ON CONFLICT DO NOTHING)
        Notification notification = Notification.builder()
                .user(user)
                .deceasedProfile(profile)
                .userProcedureChecklist(checklist)
                .message(message)
                .isRead(false)
                .idempotencyKey(idempotencyKey)
                .build();

        notificationBulkRepository.bulkInsert(List.of(notification));

        // 4. PENDING 조회 (실제 INSERT된 row만)
        List<Notification> pending = notificationRepository
                .findByIdempotencyKeysAndStatus(List.of(idempotencyKey), NotificationDeliveryStatus.PENDING);
        if (pending.isEmpty()) {
            log.info("[CategoryCompleted] 멱등키 충돌로 skip - key={}", idempotencyKey);
            return;
        }

        Notification saved = pending.get(0);

        // 5. FCM 발송
        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            notificationBulkRepository.bulkUpdateToFailed(
                    Map.of(saved.getNotificationId(), "FCM 토큰 미등록"));
            return;
        }

        FcmSendResult result = fcmSendService.send(fcmToken,
                new FcmMessagePayload(profile.getName(), message, saved.getNotificationId()));

        // 6. status 업데이트
        if (result.success()) {
            notificationBulkRepository.bulkUpdateToSent(List.of(saved.getNotificationId()));
        } else {
            notificationBulkRepository.bulkUpdateToFailed(
                    Map.of(saved.getNotificationId(), result.failureReason()));
        }

        log.info("[CategoryCompleted] 처리 완료 - notificationId={}, success={}",
                saved.getNotificationId(), result.success());
    }
}
